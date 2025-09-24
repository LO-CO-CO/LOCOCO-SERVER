package com.lokoko.domain.customer.domain.repository;

import com.lokoko.domain.customer.domain.entity.Customer;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c JOIN FETCH c.user WHERE c.id = :customerId")
    Optional<Customer> findCustomerWithUserById(@Param("customerId") Long customerId);

    boolean existsByCustomerNameIgnoreCase(String customerName);

}