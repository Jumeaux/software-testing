package com.amigoscode.testing.payment;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
public class PaymentRepositoryTest {

    @Autowired
    PaymentRepository underTest;

    @Test
    void itShouldInsertPayment(){
        // Give
        Payment payment= new Payment(1L,UUID.randomUUID(), 500L,Currency.GNF,"0988xxx","Payment cours");

        //When
        underTest.save(payment);

        //then
        Optional<Payment> optionalCustomer= underTest.findById(1L);
        assertThat(optionalCustomer).isPresent().hasValueSatisfying(p-> assertThat(p).isEqualTo(payment));
    }

}
