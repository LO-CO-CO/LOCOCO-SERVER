package com.lokoko.domain.youtube.application.service;

import com.lokoko.domain.video.domain.entity.YoutubeVideo;
import com.lokoko.domain.youtube.api.dto.TrendsYoutubeResponse;
import com.lokoko.domain.youtube.application.crawler.PopularVideoCrawler;
import com.lokoko.domain.youtube.domain.repository.YoutubeVideoRepository;
import com.lokoko.domain.youtube.mapper.YoutubeMapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class YoutubeTrendService {
    private static final List<String> BEAUTY_TOPICS = List.of(
            "2025 여름 메이크업 트렌드",
            "10단계 스킨케어 루틴",
            "인기 K-뷰티 신제품",
            "여드름 피부 커버 메이크업",
            "지성 피부 파운데이션 추천",
            "아이크림 효과 비교",
            "선크림 중요성 및 사용법",
            "화이트닝 미백 크림 추천",
            "수분 부족 피부 관리법",
            "민감성 피부 케어 방법"
    );
    private final PopularVideoCrawler crawlService;
    private final VideoSaveService saveService;
    private final YoutubeVideoRepository repository;
    private final YoutubeMapper youtubeMapper;

    @Transactional
    public void crawlPopularBeautyVideos() {
        List<YoutubeVideo> allVideos = crawlService.crawl(BEAUTY_TOPICS);

        List<YoutubeVideo> nonShortLongVideos = allVideos.stream()
                .filter(v -> v.getUrl() != null
                        && !v.getUrl().contains("/shorts/")
                        && v.getDurationSeconds() != null
                        && v.getDurationSeconds() > 60)
                .collect(Collectors.toCollection(ArrayList::new));

        nonShortLongVideos.sort(Comparator.comparingLong(YoutubeVideo::getViewCount).reversed());

        List<YoutubeVideo> top10Unique = new ArrayList<>();
        Set<String> seenUrls = new HashSet<>();

        for (YoutubeVideo video : nonShortLongVideos) {
            if (seenUrls.add(video.getUrl())) {
                top10Unique.add(video);
            }
            if (top10Unique.size() == 30) {
                break;
            }
        }
        for (int i = 0; i < top10Unique.size(); i++) {
            top10Unique.get(i).updatePopularity(i + 1);
        }

        try {
            saveService.replaceAll(top10Unique);
        } catch (Exception e) {
            log.error("[저장실패] replaceAll 에러, videos={}, error={}",
                    top10Unique, e.getMessage(), e);
            throw e;
        }
    }

    public List<TrendsYoutubeResponse> findAll() {
        List<YoutubeVideo> videos = repository.findAll();

        return youtubeMapper.toTrendsResponse(videos);
    }
}
