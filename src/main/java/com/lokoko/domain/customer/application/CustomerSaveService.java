package com.lokoko.domain.customer.application;

import com.lokoko.domain.customer.domain.entity.Customer;
import com.lokoko.domain.customer.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerSaveService {

    private final CustomerRepository customerRepository;

    @Transactional
    public Customer save(Customer customer) {

        return customerRepository.save(customer);
    }
}
