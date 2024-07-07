package com.amigoscode.testing.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;

public class PaymentServiceTest {
   
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private  CustomerRepository customerRepository;

    @Mock
    private CardPaymentCharger cardPaymentCharger;

    @Mock
    private SmsService smsService ;

    private PaymentService underTest;

    @BeforeEach
    void setUp(){

        MockitoAnnotations.initMocks(this);
        underTest= new PaymentService(paymentRepository, customerRepository, cardPaymentCharger,smsService);
    }

    @Test
    void itShouldChargeCardSuccessfully() {

        // Give
        UUID customerId=UUID.randomUUID();
        //... Customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        //...Payment Request
        Payment payment= new Payment(null, null, 1300L, Currency.GNF, "0588xxx", "Payment cours");
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        //... card is charged successfully
        given(
            cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(), 
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
            )
        ).willReturn(new CardPaymentCharge(true));

        // When
        underTest.chargeCard(customerId,paymentRequest);

        //Then
        ArgumentCaptor<Payment> paymentArgumentCaptor=ArgumentCaptor.forClass(Payment.class);
        then(paymentRepository).should().save(paymentArgumentCaptor.capture());

        Payment paymentArgumentCaptorValue=paymentArgumentCaptor.getValue();

        assertThat(paymentArgumentCaptorValue).isEqualToIgnoringGivenFields(paymentRequest.getPayment(), "customerId");

        assertThat(paymentArgumentCaptorValue.getCustomerId()).isEqualTo(customerId);
    }
    
    
    @Test
    void itShouldThrownWhenCardIsNotDebited(){

                // Give
                UUID customerId=UUID.randomUUID();
                given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));
        
                //...Payment Request
                Payment payment= new Payment(null, null, 100L, Currency.GNF, "0588xxx", "Payment cours");
                PaymentRequest paymentRequest = new PaymentRequest(payment);
        
                //... card is not charged successfully
                given(
                    cardPaymentCharger.chargeCard(
                        paymentRequest.getPayment().getSource(),
                        paymentRequest.getPayment().getAmount(), 
                        paymentRequest.getPayment().getCurrency(),
                        paymentRequest.getPayment().getDescription()
                    )
                ).willReturn(new CardPaymentCharge(false));

                // When
                //Then
                assertThatThrownBy(()-> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Card not debited for the Customer %s", customerId));

        //..  No interaction with paymentRepository
        then(paymentRepository).shouldHaveNoInteractions();

    }


    @Test
    void itShouldNotChargeCardAndThrownWhenCurrencyNotSupported(){

        // Give
        UUID customerId=UUID.randomUUID();

        // ..Customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        //...Payment Request
        Payment payment= new Payment(null, null, 200L, Currency.XOF, "0588xxx", "Payment cours");
        PaymentRequest paymentRequest = new PaymentRequest(payment);


        // When
        assertThatThrownBy(()-> underTest.chargeCard(customerId, paymentRequest))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining(String.format("the currency not supported: %s", paymentRequest.getPayment().getCurrency()));

        //Then
        //..  No interaction with cardPaymentCharger
        then(cardPaymentCharger).shouldHaveNoInteractions();

        //..  No interaction with paymentRepository
        then(paymentRepository).shouldHaveNoInteractions();
    }



    @Test
    void itShouldNotChargeCardAndThrownWhenCustomerNotFound(){
        // Give
        UUID customerId=UUID.randomUUID();

        // ..Customer not found
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(()-> underTest.chargeCard(customerId, new PaymentRequest(new Payment())))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining(String.format("Customer with ID  %s not found", customerId));

        //Then
        //..  No interaction with cardPaymentCharger
        then(cardPaymentCharger).shouldHaveNoInteractions();

        //..  No interaction with paymentRepository
        then(paymentRepository).shouldHaveNoInteractions();
    }


    @Test
    void itShouldFindPaymentByCustomer(){
        // Give
        UUID customerId=UUID.randomUUID();

        Payment payment = new Payment(1L, customerId, new BigDecimal(500), Currency.GNF, "3xx...x4", "cours Software Testing");
        Payment payment1 = new Payment(1L, customerId, new BigDecimal(500), Currency.GNF, "3xx...x455", "cours Software ");
       
        given(paymentRepository.findPaymentByCustomer(customerId)).willReturn(Arrays.asList(payment, payment1));

        // When
        List<Payment> payments=underTest.getPaymentByCustomer(customerId);

        //Then
        assertThat(payments).isNotEmpty();
        assertThat(payments.size()).isEqualTo(2);
        assertThat(payments.get(0)).isEqualTo(payment);
    }


    @Test
    void itShouldThrownWhenCustomerIdIsNull(){
        // Give
        UUID customerId=null;

        // When
        assertThatThrownBy(()->underTest.getPaymentByCustomer(customerId)
        ).isInstanceOf(IllegalStateException.class).hasMessageContaining ("Customer ID is null");
       
        //Finally
        then(paymentRepository).shouldHaveNoInteractions();
       
    }


    @Test
    void itShouldThrownWhenPaymentNotFoundByCustomer(){
        // Give
        UUID customerId=UUID.randomUUID();

        // ..Payments not found
        given(paymentRepository.findPaymentByCustomer(customerId)).willReturn(new ArrayList<Payment>());

        // When
        assertThatThrownBy(()->underTest.getPaymentByCustomer(customerId)
        ).isInstanceOf(IllegalStateException.class).hasMessageContaining (String.format("Payment not found for the customer %s", customerId));
       
       
    }


}
