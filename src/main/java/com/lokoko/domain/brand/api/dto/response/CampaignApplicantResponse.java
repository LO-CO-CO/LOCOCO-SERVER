package com.lokoko.domain.brand.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record CampaignApplicantResponse(
        @Schema(requiredMode = REQUIRED, description = "크리에이터 참여 정보 id", example = "1")
        Long applicantId,
        @Schema(requiredMode = REQUIRED, description = "크리에이터 id", example = "1")
        Long creatorId,
        @Schema(requiredMode = REQUIRED, description = "크리에이터 프로필 이미지")
        String creatorProfileImageUrl,
        @Schema(requiredMode = REQUIRED, description = "크리에이터 풀네임", example = "PARK JAMES")
        String creatorFullName,
        @Schema(requiredMode = REQUIRED, description = "크리에이터 닉네임", example = "@rookie21")
        String creatorNickName,
        @Schema(requiredMode = REQUIRED, description = "인스타그램 팔로워 수", example = "111111111")
        Integer instagramFollower,
        @Schema(requiredMode = REQUIRED, description = "틱톡 팔로워 수", example = "2222222")
        Integer tiktokFollower,
        @Schema(requiredMode = REQUIRED, description = "크리에이터가 참여한 총 캠페인 수", example = "5")
        Integer participationCount,
        @Schema(requiredMode = REQUIRED, description = "크리에이터가 캠페인에 지원한 날짜", example = "2025-09-16T00:21:04Z")
        Instant appliedDate,
        @Schema(requiredMode = REQUIRED, description = "승인 상태", example = "PENDING/APPROVED/REJECTED")
        String approveStatus
) {
}
