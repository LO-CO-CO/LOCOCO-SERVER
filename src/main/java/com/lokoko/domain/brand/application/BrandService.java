package com.lokoko.domain.brand.application;

import com.lokoko.domain.brand.api.dto.request.BrandInfoUpdateRequest;
import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.brand.domain.repository.BrandRepository;
import com.lokoko.domain.brand.exception.BrandNotFoundException;
import com.lokoko.domain.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandService {

    private final UserRepository userRepository;
    private final BrandRepository brandRepository;

    public void updateBrandInfo(Long userId, BrandInfoUpdateRequest request) {
        Brand brand = brandRepository.findById(userId)
                .orElseThrow(BrandNotFoundException::new);

        brand.assignBrandName(request.brandName());
        brand.assignManagerName(request.managerName());
        brand.assignManagerPosition(request.managerPosition());
        brand.assignPhoneNumber("+82" + request.phoneNumber());
        brand.assignRoadAddress(request.roadAddress());
        brand.assignAddressDetail(request.addressDetail());

        brandRepository.save(brand);
    }
}