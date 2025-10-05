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
import com.lokoko.global.auth.provider.insta.dto.*;
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

            // 1) code → Facebook 액세스 토큰
            FacebookAccessTokenDto facebookToken = instaOAuthClient.exchangeFacebookToken(code);

            // 2) Facebook 페이지 목록 조회
            FacebookPagesDto pages = instaOAuthClient.getFacebookPages(facebookToken.accessToken());

            // 3) 각 페이지에서 Instagram 비즈니스 계정 찾기
            String instagramUserId = null;
            String pageAccessToken = null;

            for (FacebookPagesDto.FacebookPage page : pages.data()) {
                InstagramBusinessAccountDto igAccount =
                        instaOAuthClient.getInstagramBusinessAccount(page.id(), page.accessToken());

                if (igAccount != null && igAccount.instagramBusinessAccount() != null) {
                    instagramUserId = igAccount.instagramBusinessAccount().id();
                    pageAccessToken = page.accessToken();
                    log.info("Instagram 비즈니스 계정 발견: userId={}, pageId={}", instagramUserId, page.id());
                    break;
                }
            }

            if (instagramUserId == null) {
                log.error("연동된 Instagram 비즈니스 계정을 찾을 수 없습니다. Facebook 페이지에 Instagram이 연결되어 있는지 확인하세요.");
                throw new InstagramConnectionFailedException();
            }

            // 4) 장기 액세스 토큰 발급 (Page Access Token을 장기 토큰으로 변환)
            InstagramGraphTokenDto longToken = instaOAuthClient.exchangeLongLivedToken(pageAccessToken);

            // 5) Redis 보관
            instaRedisTokenService.saveLongLivedToken(userId, longToken.accessToken(), longToken.expiresIn());

            // 6) 계정 연결 (Creator / Customer)
            if (user.getRole() == Role.CREATOR) {
                user.getCreator().connectInsta(instagramUserId);
                creatorSaveService.save(user.getCreator());
            } else if (user.getRole() == Role.CUSTOMER) {
                user.getCustomer().connectInsta(instagramUserId);
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
