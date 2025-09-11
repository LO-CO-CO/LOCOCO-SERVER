package com.lokoko.domain.creator.application;

import com.lokoko.domain.creator.api.dto.request.CreatorInfoUpdateRequest;
import com.lokoko.domain.creator.api.dto.response.CreatorInfoResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorRegisterCompleteResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorSnsConnectedResponse;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.domain.repository.CreatorRepository;
import com.lokoko.domain.creator.exception.CreatorInfoNotCompletedException;
import com.lokoko.domain.creator.exception.CreatorIdAlreadyExistsException;
import com.lokoko.domain.creator.exception.CreatorNotFoundException;
import com.lokoko.domain.creator.exception.SnsNotConnectedException;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import com.lokoko.global.auth.exception.InvalidRoleException;
import com.lokoko.global.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreatorRegisterService {
    private final CreatorRepository creatorRepository;
    private final UserRepository userRepository;
    private final AuthService authService;


    public void validateCreatorId(String creatorName, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Creator creator = user.getCreator();

        if (creator != null &&
                creator.getCreatorName() != null &&
                creator.getCreatorName().equalsIgnoreCase(creatorName)) {
            return;
        }

        if (creatorRepository.existsByCreatorName(creatorName)) {
            throw new CreatorIdAlreadyExistsException();
        }
    }

    @Transactional
    public void updateCreatorInfo(Long userId, CreatorInfoUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Creator creator = user.getCreator();
        if (creator == null) {
            throw new CreatorNotFoundException();
        }

        if (shouldCheckDuplicateCreatorName(creator, request.creatorName())) {
            validateCreatorId(request.creatorName(), userId);
        }

        creator.setCreatorName(request.creatorName());
        creator.setBirthDate(request.birthDate());
        creator.setGender(request.gender());
        creator.setFirstName(request.firstName());
        creator.setLastName(request.lastName());
        creator.setCountryCode(request.countryCode());
        creator.setPhoneNumber(request.phoneNumber());
        creator.setContentLanguage(request.contentLanguage());
        creator.setCountry(request.country());
        creator.setStateOrProvince(request.stateOrProvince());
        creator.setCityOrTown(request.cityOrTown());
        creator.setAddressLine1(request.addressLine1());
        creator.setAddressLine2(request.addressLine2());
        creator.setPostalCode(request.postalCode());
        creator.setSkinType(request.skinType());
        creator.setSkinTone(request.skinTone());

        creatorRepository.save(creator);
    }

    private boolean shouldCheckDuplicateCreatorName(Creator creator, String newName) {
        return creator.getCreatorName() == null ||
                !creator.getCreatorName().equalsIgnoreCase(newName);
    }


    public CreatorSnsConnectedResponse validateCreatorSnsConnected(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Creator creator = user.getCreator();

        return new CreatorSnsConnectedResponse(creator.getInstaLink() != null, creator.getTiktokLink() != null);


    }

    public CreatorInfoResponse getCreatorInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Creator creator = user.getCreator();

        if (creator == null) {
            throw new CreatorNotFoundException();
        }

        return new CreatorInfoResponse(
                creator.getCreatorName(),
                creator.getBirthDate(),
                creator.getGender(),
                creator.getFirstName(),
                creator.getLastName(),
                creator.getCountryCode(),
                creator.getPhoneNumber(),
                creator.getContentLanguage(),
                creator.getCountry(),
                creator.getStateOrProvince(),
                creator.getCityOrTown(),
                creator.getAddressLine1(),
                creator.getAddressLine2(),
                creator.getPostalCode(),
                creator.getSkinType(),
                creator.getSkinTone()
        );
    }

    @Transactional
    public CreatorRegisterCompleteResponse completeCreatorSignup(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (user.getRole() != Role.CREATOR) {
            throw new InvalidRoleException();
        }

        OauthLoginStatus currentStatus = authService.getCreatorStatus(user);

        // SNS_REQUIRED 상태가 아니면 에러
        if (currentStatus != OauthLoginStatus.SNS_REQUIRED) {
            if (currentStatus == OauthLoginStatus.INFO_REQUIRED) {
                throw new CreatorInfoNotCompletedException();
            }
        }

        // Creator 엔티티에서 SNS 연동 재확인
        Creator creator = user.getCreator();
        boolean hasInstagram = creator.getInstaLink() != null && !creator.getInstaLink().isBlank();
        boolean hasTiktok = creator.getTiktokLink() != null && !creator.getTiktokLink().isBlank();

        if (!hasInstagram && !hasTiktok) {
            throw new SnsNotConnectedException();
        }

        return new CreatorRegisterCompleteResponse(OauthLoginStatus.LOGIN);
    }
}
