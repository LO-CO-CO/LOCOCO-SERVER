package com.lokoko.domain.user.application.service;


import com.lokoko.domain.creator.domain.repository.CreatorRepository;
import com.lokoko.domain.customer.domain.repository.CustomerRepository;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.domain.user.exception.UserIdAlreadyExistsException;
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
}