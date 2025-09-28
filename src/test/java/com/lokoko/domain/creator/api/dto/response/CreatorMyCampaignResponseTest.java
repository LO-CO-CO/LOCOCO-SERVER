package com.lokoko.domain.creator.api.dto.response;

import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewStatus;
import com.lokoko.domain.creator.util.CampaignStatusMapper;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreatorMyCampaignResponseTest {

    @Test
    @DisplayName("Factory method로 응답 객체 생성 테스트")
    void createResponseWithFactoryMethod() {
        // given
        Long campaignId = 1L;
        String title = "테스트 캠페인";
        Instant deadline = Instant.now();
        ParticipationStatus participationStatus = ParticipationStatus.ACTIVE;
        List<CampaignStatusMapper.ReviewInfo> reviews = List.of(
                new CampaignStatusMapper.ReviewInfo(1L, ReviewRound.FIRST, ContentType.TIKTOK_VIDEO, ReviewStatus.SUBMITTED, Instant.now())
        );
        List<ContentType> requiredContentTypes = List.of(ContentType.TIKTOK_VIDEO, ContentType.INSTA_REELS);

        // when
        CreatorMyCampaignResponse response = CreatorMyCampaignResponse.of(
                campaignId, title, deadline, participationStatus, reviews, requiredContentTypes
        );

        // then
        assertNotNull(response);
        assertEquals(campaignId, response.campaignId());
        assertEquals(title, response.title());
        assertEquals(deadline, response.reviewSubmissionDeadline());
        assertEquals("진행중", response.overallStatus());
        assertEquals("Upload 1st Review", response.nextAction());
        assertEquals(participationStatus, response.participationStatus());
    }

    @Test
    @DisplayName("간단한 Factory method로 응답 객체 생성 테스트")
    void createSimpleResponseWithFactoryMethod() {
        // given
        Long campaignId = 1L;
        String title = "테스트 캠페인";
        Instant deadline = Instant.now();
        ParticipationStatus participationStatus = ParticipationStatus.PENDING;

        // when
        CreatorMyCampaignResponse response = CreatorMyCampaignResponse.ofSimple(
                campaignId, title, deadline, participationStatus
        );

        // then
        assertNotNull(response);
        assertEquals(campaignId, response.campaignId());
        assertEquals(title, response.title());
        assertEquals(deadline, response.reviewSubmissionDeadline());
        assertEquals("승인 대기", response.overallStatus());
        assertEquals("브랜드 승인 대기", response.nextAction());
        assertEquals(participationStatus, response.participationStatus());
    }

    @Test
    @DisplayName("수정 요청 상태의 응답 객체 생성 테스트")
    void createResponseWithRevisionRequested() {
        // given
        Long campaignId = 1L;
        String title = "테스트 캠페인";
        Instant deadline = Instant.now();
        ParticipationStatus participationStatus = ParticipationStatus.ACTIVE;
        List<CampaignStatusMapper.ReviewInfo> reviews = List.of(
                new CampaignStatusMapper.ReviewInfo(1L, ReviewRound.FIRST, ContentType.TIKTOK_VIDEO, ReviewStatus.REVISION_REQUESTED, Instant.now())
        );
        List<ContentType> requiredContentTypes = List.of(ContentType.TIKTOK_VIDEO);

        // when
        CreatorMyCampaignResponse response = CreatorMyCampaignResponse.of(
                campaignId, title, deadline, participationStatus, reviews, requiredContentTypes
        );

        // then
        assertEquals("수정 요청", response.overallStatus());
        assertEquals("요청된 내용 수정 후 재제출", response.nextAction());
    }
}