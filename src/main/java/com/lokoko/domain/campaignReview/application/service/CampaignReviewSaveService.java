package com.lokoko.domain.campaignReview.application.service;

import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.repository.CampaignReviewRepository;
import com.lokoko.domain.image.domain.entity.CampaignReviewImage;
import com.lokoko.domain.image.domain.repository.CampaignReviewImageRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampaignReviewSaveService {

    private final CampaignReviewRepository campaignReviewRepository;
    private final CampaignReviewImageRepository campaignReviewImageRepository;

    @Transactional
    public CampaignReview saveReview(CampaignReview campaignReview) {
        return campaignReviewRepository.save(campaignReview);
    }

    @Transactional
    public void saveImages(CampaignReview campaignReview, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        List<CampaignReviewImage> images = new ArrayList<>(imageUrls.size());
        for (String url : imageUrls) {
            images.add(
                    CampaignReviewImage.builder()
                            .url(url)
                            .campaignReview(campaignReview)
                            .build()
            );
        }
        campaignReviewImageRepository.saveAll(images);
    }
}
