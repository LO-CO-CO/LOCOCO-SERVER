package com.lokoko.domain.campaignReview.application.mapper;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaignReview.api.dto.request.FirstReviewUploadRequest;
import com.lokoko.domain.campaignReview.api.dto.request.SecondReviewUploadRequest;
import com.lokoko.domain.campaignReview.api.dto.response.CampaignReviewDetailResponse;
import com.lokoko.domain.campaignReview.api.dto.response.ReviewUploadResponse;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReviewImage;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReviewVideo;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.media.api.dto.repsonse.MediaPresignedUrlResponse;
import com.lokoko.domain.media.domain.MediaFile;
import com.lokoko.global.utils.S3UrlParser;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CampaignReviewMapper {

    public CampaignReview toFirstReview(CreatorCampaign creatorCampaign,
                                        FirstReviewUploadRequest request) {
        CampaignReview campaignReview = new CampaignReview();
        campaignReview.bindToCreatorCampaign(creatorCampaign);
        campaignReview.designateRound(ReviewRound.FIRST);
        campaignReview.chooseContentType(request.contentType());
        campaignReview.requestFirstReview(request.captionWithHashtags());
        return campaignReview;
    }

    public CampaignReview toSecondReview(CreatorCampaign creatorCampaign,
                                         SecondReviewUploadRequest request) {
        CampaignReview campaignReview = new CampaignReview();
        campaignReview.bindToCreatorCampaign(creatorCampaign);
        campaignReview.designateRound(ReviewRound.SECOND);
        campaignReview.chooseContentType(request.contentType());
        campaignReview.requestSecondReview(request.captionWithHashtags(), request.postUrl());
        return campaignReview;
    }

    public List<CampaignReviewImage> toCampaignReviewImages(List<String> mediaUrls,
                                                            CampaignReview campaignReview) {
        List<CampaignReviewImage> images = new ArrayList<>(mediaUrls.size());
        for (int displayOrder = 0; displayOrder < mediaUrls.size(); displayOrder++) {
            MediaFile mediaFile = S3UrlParser.parsePresignedUrl(mediaUrls.get(displayOrder));
            images.add(CampaignReviewImage.builder()
                    .mediaFile(mediaFile)
                    .displayOrder(displayOrder)
                    .campaignReview(campaignReview)
                    .build());
        }

        return images;
    }

    public List<CampaignReviewVideo> toCampaignReviewVideos(List<String> mediaUrls,
                                                            CampaignReview campaignReview) {
        List<CampaignReviewVideo> videos = new ArrayList<>(mediaUrls.size());
        for (int displayOrder = 0; displayOrder < mediaUrls.size(); displayOrder++) {
            MediaFile mediaFile = S3UrlParser.parsePresignedUrl(mediaUrls.get(displayOrder));
            videos.add(CampaignReviewVideo.builder()
                    .mediaFile(mediaFile)
                    .displayOrder(displayOrder)
                    .campaignReview(campaignReview)
                    .build());
        }

        return videos;
    }

    public ReviewUploadResponse toUploadResponse(CampaignReview saved) {

        return ReviewUploadResponse.builder()
                .reviewId(saved.getId())
                .build();
    }

    public CampaignReviewDetailResponse toCampaignReviewDetailResponse(
            Campaign campaign,
            CampaignReview review,
            List<String> mediaUrls
    ) {
        return CampaignReviewDetailResponse.builder()
                .campaignId(campaign.getId())
                .title(campaign.getTitle())
                .contentType(review.getContentType())
                .mediaUrls(mediaUrls)
                .captionWithHashtags(review.getCaptionWithHashtags())
                .postUrl(review.getPostUrl())
                .build();
    }

    public MediaPresignedUrlResponse toMediaPresignedUrlResponse(List<String> presignedUrls) {
        return MediaPresignedUrlResponse.builder()
                .mediaUrl(presignedUrls)
                .build();
    }
}
