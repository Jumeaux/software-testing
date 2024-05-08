package com.amigoscode.testing.customer;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer,UUID> {
    

    @Query(value = "select id, name, phone_number from customer where phone_number=:phone_number",nativeQuery = true)
    Optional<Customer> findByCustomerByPhoneNumber(@Param("phone_number") String phoneNumber);
}
