package com.lokoko.domain.productBrand.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ProductBrandInfoResponse(

	@Schema(requiredMode = REQUIRED, description = "상품 브랜드명")
	String productBrandName,

	@Schema(requiredMode = REQUIRED, description = "상품명")
	String productName,

	@Schema(requiredMode = REQUIRED, description = "용량")
	String unit,

	@Schema(requiredMode = REQUIRED, description = "평균 별점")
	Double rating,

	@Schema(requiredMode = REQUIRED, description = "대표 이미지 URL")
	String imageUrl
) {
}

