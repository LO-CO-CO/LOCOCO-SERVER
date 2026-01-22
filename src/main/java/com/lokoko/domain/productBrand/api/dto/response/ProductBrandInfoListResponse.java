package com.lokoko.domain.productBrand.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.*;

import java.util.List;

import com.lokoko.global.common.response.PageableResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ProductBrandInfoListResponse(

	@Schema(requiredMode = REQUIRED, description = "상품 목록")
	List<ProductBrandInfoResponse> products,

	@Schema(requiredMode = REQUIRED, description = "페이지 정보")
	PageableResponse pageInfo
) {
}
