package com.lokoko.global.auth.provider.insta.usecase;

import com.lokoko.domain.creator.application.service.CreatorSaveService;
import com.lokoko.domain.customer.application.CustomerSaveService;
import com.lokoko.domain.user.application.service.UserGetService;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.global.auth.exception.InstagramConnectionFailedException;
import com.lokoko.global.auth.exception.InstagramLongTokenRequestFailedException;
import com.lokoko.global.auth.exception.InstagramTokenRequestFailedException;
import com.lokoko.global.auth.provider.insta.config.InstaOauthClient;
import com.lokoko.global.auth.provider.insta.dto.InstagramConnectionResponse;
import com.lokoko.global.auth.provider.insta.dto.InstagramLongTokenDto;
import com.lokoko.global.auth.provider.insta.dto.InstagramShortTokenDto;
import com.lokoko.global.auth.provider.insta.service.InstaRedisTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstaConnectionUsecase {

    private final InstaOauthClient instaOAuthClient;
    private final UserGetService userGetService;

    private final CustomerSaveService customerSaveService;
    private final CreatorSaveService creatorSaveService;
    private final InstaRedisTokenService instaRedisTokenService;

    @Transactional(readOnly = true)
    public String buildAuthorizationUrl(Long userId, String returnTo) {
        userGetService.findUserById(userId);

        return instaOAuthClient.buildAuthorizationUrl(userId, returnTo);
    }

    @Transactional
    public InstagramConnectionResponse connectInstagram(Long userId, String code, String returnTo) {
        try {
            User user = userGetService.findUserById(userId);

            // 1) code → 단기 토큰
            InstagramShortTokenDto shortDto = instaOAuthClient.exchangeShortLivedToken(code);

            // 2) 단기 → 장기 토큰
            InstagramLongTokenDto longDto = instaOAuthClient.exchangeLongLivedToken(shortDto.accessToken());

            // 3) Redis 보관
            instaRedisTokenService.saveLongLivedToken(userId, longDto.accessToken(), longDto.expiresIn());

            // 4) 계정 연결 (Creator / Customer)
            String instaUserId = String.valueOf(shortDto.userId());
            if (user.getRole() == Role.CREATOR) {
                user.getCreator().connectInsta(instaUserId);
                creatorSaveService.save(user.getCreator());
            } else if (user.getRole() == Role.CUSTOMER) {
                user.getCustomer().connectInsta(instaUserId);
                customerSaveService.save(user.getCustomer());
            }

            return InstagramConnectionResponse.connected(returnTo);

        } catch (InstagramTokenRequestFailedException | InstagramLongTokenRequestFailedException e) {
            throw e;
        } catch (Exception e) {
            log.error("인스타 연결 실패 {}: {}", userId, e.getMessage(), e);
            throw new InstagramConnectionFailedException();
        }
    }
}
