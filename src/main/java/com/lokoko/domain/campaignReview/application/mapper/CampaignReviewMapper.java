package com.lokoko.domain.campaignReview.application.mapper;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaignReview.api.dto.response.CampaignReviewDetailListResponse;
import com.lokoko.domain.campaignReview.api.dto.response.CampaignReviewDetailResponse;
import com.lokoko.domain.campaignReview.api.dto.response.ReviewUploadResponse;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReviewImage;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReviewVideo;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.media.api.dto.response.MediaPresignedUrlResponse;
import com.lokoko.domain.media.domain.MediaFile;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.global.utils.S3UrlParser;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class CampaignReviewMapper {

    public CampaignReview toFirstReview(CreatorCampaign creatorCampaign, ContentType contentType,
                                        String captionWithHashtags) {
        CampaignReview review = new CampaignReview();
        review.bindToCreatorCampaign(creatorCampaign);
        review.designateRound(ReviewRound.FIRST);
        review.chooseContentType(contentType);
        review.requestFirstReview(captionWithHashtags);
        return review;
    }

    public CampaignReview toSecondReview(CreatorCampaign creatorCampaign, ContentType contentType,
                                         String captionWithHashtags, String postUrl) {
        CampaignReview review = new CampaignReview();
        review.bindToCreatorCampaign(creatorCampaign);
        review.designateRound(ReviewRound.SECOND);
        review.chooseContentType(contentType);
        review.requestSecondReview(captionWithHashtags, postUrl);
        return review;
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

    public MediaPresignedUrlResponse toMediaPresignedUrlResponse(List<String> urls) {
        return new MediaPresignedUrlResponse(urls);
    }

    public CampaignReviewDetailResponse toDetailResponse(CampaignReview review, List<String> mediaUrls) {
        return CampaignReviewDetailResponse.builder()
                .contentType(review.getContentType())
                .mediaUrls(mediaUrls)
                .captionWithHashtags(review.getCaptionWithHashtags())
                .postUrl(review.getPostUrl())
                .build();
    }

    public CampaignReviewDetailListResponse toDetailListResponse(Campaign campaign, CampaignReview review,
                                                                 ReviewRound round,
                                                                 List<String> mediaUrls,
                                                                 String postUrl) {

        // 브랜드 노트 마감일은 캠페인 deadLine으로부터 4일 전
        Instant brandNoteDeadline = campaign.getApplyDeadline().minus(Duration.ofDays(4));

        return CampaignReviewDetailListResponse.builder()
                .campaignId(campaign.getId())
                .title(campaign.getTitle())
                .reviewRound(round)
                .contentType(review.getContentType())
                .reviewImages(mediaUrls)
                .captionWithHashtags(review.getCaptionWithHashtags())
                .brandNote(review.getBrandNote())
                .brandNoteDeadline(brandNoteDeadline)
                .postUrl(postUrl)
                .build();
    }
}
