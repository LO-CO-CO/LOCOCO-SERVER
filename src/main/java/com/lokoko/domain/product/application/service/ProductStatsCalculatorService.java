package com.lokoko.domain.product.application.service;

import com.lokoko.domain.product.api.dto.response.RatingPercentResponse;
import com.lokoko.domain.review.dto.request.RatingCount;
import com.lokoko.domain.review.entity.enums.Rating;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductStatsCalculatorService {

    /**
     * 주어진 리뷰 통계 리스트에서 제품별 리뷰 개수를 합산하여 반환
     *
     * @param stats 리뷰별 count 정보 리스트
     * @return 제품 ID를 키로, 리뷰 개수를 값으로 하는 맵
     */
    public Map<Long, Long> calculateReviewCount(List<RatingCount> stats) {
        return stats.stream()
                .collect(Collectors.groupingBy(
                        RatingCount::productId,
                        Collectors.summingLong(RatingCount::count)
                ));
    }

    /**
     * 주어진 리뷰 통계 리스트에서 제품별 계산합(별점 값 * 개수)를 반환
     *
     * @param stats 리뷰별 count 정보 리스트
     * @return 제품 ID를 키로, (별점 값 * 개수) 합을 값으로 하는 맵
     */
    public Map<Long, Long> calculateWeightedSum(List<RatingCount> stats) {
        return stats.stream()
                .collect(Collectors.groupingBy(RatingCount::productId,
                        Collectors.summingLong(rc -> rc.rating().getValue() * rc.count())
                ));
    }

    /**
     * 제품별 리뷰 개수와 가중합을 기반으로 평균 평점을 계산하여 반환
     *
     * @param countMap 제품별 리뷰 개수 맵
     * @param sumMap   제품별 가중합 맵
     * @return 제품 ID를 키로, 평균 평점을 소수점 첫째 자리까지 반올림하여 값으로 하는 맵
     */
    public Map<Long, Double> calculateAvgRating(Map<Long, Long> countMap,
                                                Map<Long, Long> sumMap) {
        return countMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            long total = e.getValue();
                            return total == 0 ? 0.0
                                    : Math.round((double) sumMap.get(e.getKey()) / total * 10) / 10.0;
                        }
                ));
    }

    /**
     * 주어진 리뷰 통계 리스트에서 별점(1~5)별 비율을 계산하여 반환
     *
     * @param stats 리뷰별 count 정보 리스트
     * @return 각 별점 값(1~5)을 키로, 전체 리뷰 대비 비율(%)을 값으로 하는 리스트
     */
    public List<RatingPercentResponse> calculateRatingPercent(List<RatingCount> stats) {
        Map<Rating, Long> countMap = stats.stream()
                .collect(Collectors.groupingBy(
                        RatingCount::rating,
                        () -> new EnumMap<>(Rating.class),
                        Collectors.summingLong(RatingCount::count)
                ));
        long total = countMap.values().stream().mapToLong(Long::longValue).sum();

        return Arrays.stream(Rating.values())
                .map(r -> {
                    long cnt = countMap.getOrDefault(r, 0L);
                    long pct = total == 0
                            ? 0L
                            : Math.round(cnt * 100.0 / total);
                    return new RatingPercentResponse(r.getValue(), pct);
                })
                .toList();
    }
}
