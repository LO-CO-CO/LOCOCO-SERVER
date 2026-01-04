package com.lokoko.domain.user.api.dto.response;

import com.lokoko.domain.creator.api.dto.response.CreatorInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record AdminCreator(
        @Schema(requiredMode = REQUIRED, description = "크리에이터 기본 정보")
        CreatorInfo creator,
        @Schema(requiredMode = REQUIRED, description = "팔로워 수 정보")
        FollowerCount followerCount,
        @Schema(requiredMode = REQUIRED, description = "크리에이터가 참여한 총 캠페인 수", example = "10")
        Integer participationCount,
        @Schema(requiredMode = REQUIRED, description = "크리에이터가 가입 완료한 시간", example = "2025-09-27T12:45:01.455391")
        Instant signupCompletedDate,
        @Schema(requiredMode = REQUIRED, description = "승인 상태", example = "PENDING")
        String approveStatus
) {
    public record FollowerCount(
            @Schema(requiredMode = REQUIRED, description = "인스타그램 팔로워 수", example = "3859")
            Integer instagramFollower,
            @Schema(requiredMode = REQUIRED, description = "틱톡 팔로워 수", example = "110089")
            Integer tiktokFollower
    ) {
    }
}
