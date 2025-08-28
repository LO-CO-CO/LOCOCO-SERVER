package com.lokoko.domain.customer.domain.repository;

import com.lokoko.domain.customer.domain.entity.Customer;
import com.lokoko.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByLineId(String lineId);
    Optional<Customer> findByGoogleId(String googleId);

}