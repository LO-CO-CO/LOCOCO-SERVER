package com.lokoko.domain.campaignReview.application.service;

import static com.lokoko.domain.productReview.application.service.ReviewService.validateMediaFiles;

import com.lokoko.domain.campaignReview.application.mapper.CampaignReviewMapper;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.repository.CampaignReviewImageRepository;
import com.lokoko.domain.campaignReview.domain.repository.CampaignReviewRepository;
import com.lokoko.domain.campaignReview.domain.repository.CampaignReviewVideoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampaignReviewSaveService {

    private final CampaignReviewRepository campaignReviewRepository;
    private final CampaignReviewImageRepository campaignReviewImageRepository;
    private final CampaignReviewVideoRepository campaignReviewVideoRepository;

    private final CampaignReviewMapper campaignReviewMapper;

    @Transactional
    public CampaignReview saveReview(CampaignReview campaignReview) {

        return campaignReviewRepository.save(campaignReview);
    }

    @Transactional
    public void saveMedia(CampaignReview campaignReview, List<String> mediaUrls) {
        if (mediaUrls == null || mediaUrls.isEmpty()) {
            return;
        }

        validateMediaFiles(mediaUrls);

        boolean isVideo = mediaUrls.get(0).contains("/video/");
        if (isVideo) {
            campaignReviewVideoRepository.saveAll(
                    campaignReviewMapper.toCampaignReviewVideos(mediaUrls, campaignReview)
            );
        } else {
            campaignReviewImageRepository.saveAll(
                    campaignReviewMapper.toCampaignReviewImages(mediaUrls, campaignReview)
            );
        }
    }
}
