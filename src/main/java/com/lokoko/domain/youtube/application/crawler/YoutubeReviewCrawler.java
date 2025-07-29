package com.lokoko.domain.youtube.application.crawler;

import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.exception.ProductNotFoundException;
import com.lokoko.domain.product.repository.ProductRepository;
import com.lokoko.domain.youtube.application.service.YoutubeApiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class YoutubeReviewCrawler {
    private final YoutubeApiService youtubeApiService;
    private final ProductRepository productRepository;

    public List<String> crawlAndStoreReviews(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        List<String> videoUrls = youtubeApiService.searchReviewVideos(
                product.getProductKoreanName()
        );

        if (!videoUrls.isEmpty()) {
            product.updateYoutubeUrls(videoUrls);
            productRepository.save(product);
            log.info("API로 수집된 {}개의 URL 저장", videoUrls.size());
        }
        return videoUrls;
    }
}
