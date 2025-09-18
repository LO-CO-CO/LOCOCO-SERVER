package com.lokoko.domain.user.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserIdCheckRequest(
        @NotBlank(message = "사용자 ID는 필수입니다")
        @Size(min = 1, max = 15, message = "사용자 ID는 1자 이상 15자 이하여야 합니다")
        @Pattern(regexp = "^[a-z0-9._]+$",
                message = "사용자 ID는 소문자 영문, 숫자, 점(.), 언더바(_)만 사용 가능합니다")
        String userId
) {}
