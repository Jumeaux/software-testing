package com.amigoscode.testing.payment;

import java.util.UUID;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    

    @Query(value = "select * from payment where customer_id=:customer_id", nativeQuery = true)
    public List<Payment> findPaymentByCustomer(@Param("customer_id") UUID customerId );
}
