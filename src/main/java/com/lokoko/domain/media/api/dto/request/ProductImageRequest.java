package com.lokoko.domain.media.api.dto.request;

import com.lokoko.domain.media.image.domain.entity.enums.ImageType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public record ProductImageRequest(

        @NotBlank(message = "URL 은 필수입니다")
        @URL(message = "올바른 URL 형식이 아닙니다.")
        String url,

        @Min(value = 0, message = "이미지 표시 순서는 음수가 될 수 없습니다.")
        int displayOrder,

        @NotNull(message = "이미지 타입은 필수입니다.")
        ImageType imageType
) {
}