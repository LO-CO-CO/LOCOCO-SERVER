package com.lokoko.domain.brand.application.service;

import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.brand.domain.repository.BrandRepository;
import com.lokoko.domain.brand.exception.BrandNotFoundException;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandGetService {

    private final BrandRepository brandRepository;
    private final UserRepository userRepository;

    public Brand findByUserId(Long userId) {
        return brandRepository.findByUserId(userId)
                .orElseThrow(BrandNotFoundException::new);
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    public Brand getBrandById(Long brandId) {
        return brandRepository.findById(brandId)
                .orElseThrow(BrandNotFoundException::new);
    }

    public Brand getBrandWithUserById(Long brandId) {
        return brandRepository.findBrandWithUserById(brandId)
                .orElseThrow(BrandNotFoundException::new);
    }
}
