package com.lokoko.domain.user.application.service;


import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.brand.domain.repository.BrandRepository;
import com.lokoko.domain.brand.exception.BrandNotFoundException;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.domain.repository.CreatorRepository;
import com.lokoko.domain.creator.exception.CreatorNotFoundException;
import com.lokoko.domain.customer.domain.entity.Customer;
import com.lokoko.domain.customer.domain.repository.CustomerRepository;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.domain.user.exception.AdminNotFoundException;
import com.lokoko.domain.user.exception.UserIdAlreadyExistsException;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.global.auth.exception.InvalidRoleException;
import com.lokoko.global.auth.exception.UserNotCompletedSignUpException;
import com.lokoko.global.auth.provider.google.dto.response.AfterLoginUserNameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final CreatorRepository creatorRepository;
    private final CustomerRepository customerRepository;
    private final BrandRepository brandRepository;

    public void checkUserIdAvailable(String requestedUserId, Long currentUserId) {
        String currentUserName = userRepository.findCurrentUserName(currentUserId);

        if (currentUserName != null && currentUserName.equalsIgnoreCase(requestedUserId)) {
            return;
        }

        boolean existsInCreator = creatorRepository.existsByCreatorNameIgnoreCase(requestedUserId);
        boolean existsInCustomer = customerRepository.existsByCustomerNameIgnoreCase(requestedUserId);

        if (existsInCreator || existsInCustomer) {
            throw new UserIdAlreadyExistsException();
        }
    }

    @Transactional(readOnly = true)
    public AfterLoginUserNameResponse getUserName(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        String displayName;
        Role role = user.getRole();

        switch (role) {
            case ADMIN:
                User admin = userRepository.findById(userId).orElseThrow(AdminNotFoundException::new);
                displayName = admin.getFirstName() + " " + admin.getLastName();
                break;
            case CUSTOMER:
                Customer customer = customerRepository.findById(userId).orElse(null);
                if (customer != null && customer.getCustomerName() != null) {
                    displayName = customer.getCustomerName();
                } else {
                    displayName = user.getFirstName() + " " + user.getLastName();
                }
                break;

            case CREATOR:
                Creator creator = creatorRepository.findById(userId)
                        .orElseThrow(CreatorNotFoundException::new);

                // Creator 필수 필드가 채워지지 않은 경우 (INFO_REQUIRED)
                if (creator.getCreatorName() == null) {
                    throw new UserNotCompletedSignUpException();
                }

                // 1개 이상의 SNS가 연동되지 않은 상태 (SNS_REQUIRED)
                if (creator.getInstagramUserId() == null && creator.getTikTokUserId() == null) {
                    throw new UserNotCompletedSignUpException();
                }

                // LOGIN 상태 검증 완료
                displayName = creator.getCreatorName();
                break;

            case BRAND:
                Brand brand = brandRepository.findById(userId)
                        .orElseThrow(BrandNotFoundException::new);

                if (brand.getBrandName() == null) {
                    throw new UserNotCompletedSignUpException();
                }
                displayName = brand.getBrandName();
                break;

            case PENDING:
                throw new UserNotCompletedSignUpException();

            default:
                throw new InvalidRoleException();
        }

        return new AfterLoginUserNameResponse(displayName, role);
    }
}