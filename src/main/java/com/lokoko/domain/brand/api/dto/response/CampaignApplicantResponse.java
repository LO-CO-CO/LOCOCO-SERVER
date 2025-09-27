package com.lokoko.domain.brand.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record CampaignApplicantResponse(
        @Schema(requiredMode = REQUIRED, description = "크리에이터 참여 정보 id", example = "1")
        Long creatorCampaignId,
        @Schema(requiredMode = REQUIRED, description = "크리에이터 id", example = "3845")
        Long creatorId,
        @Schema(requiredMode = REQUIRED, description = "크리에이터 기본 정보")
        CreatorInfo creator,
        @Schema(requiredMode = REQUIRED, description = "팔로워 수 정보")
        FollowerCount followerCount,
        @Schema(requiredMode = REQUIRED, description = "크리에이터가 참여한 총 캠페인 수", example = "10")
        Integer participationCount,
        @Schema(requiredMode = REQUIRED, description = "크리에이터가 캠페인에 지원한 날짜", example = "2025-09-27T12:45:01.455391")
        Instant appliedDate,
        @Schema(requiredMode = REQUIRED, description = "승인 상태", example = "PENDING")
        String approveStatus
) {
    public record CreatorInfo(
            @Schema(requiredMode = REQUIRED, description = "크리에이터 풀네임", example = "James Rodriguez")
            String creatorFullName,
            @Schema(requiredMode = REQUIRED, description = "크리에이터 닉네임", example = "echandler")
            String creatorNickName,
            @Schema(requiredMode = REQUIRED, description = "크리에이터 프로필 이미지")
            String creatorProfileImageUrl
    ) {}

    public record FollowerCount(
            @Schema(requiredMode = REQUIRED, description = "인스타그램 팔로워 수", example = "3859")
            Integer instagramFollower,
            @Schema(requiredMode = REQUIRED, description = "틱톡 팔로워 수", example = "110089")
            Integer tiktokFollower
    ) {}
}
