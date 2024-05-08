package com.amigoscode.testing.customer;


import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.assertj.core.api.Assertions.*;


public class CustomerRegistrationServiceTest {
    
    @Mock
    private CustomerRepository customerRepository;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    private CustomerRegistrationService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest=new CustomerRegistrationService(customerRepository);
    }

    @Test
    void itShouldSaveNewCustomer(){

        //Given a phone number and customer
        String phoneNumber="+224758000";
        Customer newCustomer= new Customer(UUID.randomUUID(), "Adam", phoneNumber);
    
        // a request
        CustomerRegistrationRequest request= new CustomerRegistrationRequest(newCustomer);
    
        // no customer with phone number passed
        given(customerRepository.findByCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
    
        // when
        underTest.registerNewCustomer(request);

        // then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentsCaptorValue=customerArgumentCaptor.getValue();

       assertThat(customerArgumentsCaptorValue).isEqualTo(newCustomer);
    }



    @Test
    void itShouldNotSaveCustomerWhenCustomerExists(){

         //Given a phone number and customer
         String phoneNumber="+224758000";
         Customer newCustomer= new Customer(UUID.randomUUID(), "Adam", phoneNumber);
     
         // a request
         CustomerRegistrationRequest request= new CustomerRegistrationRequest(newCustomer);
     
         // an existing customer with phone number
         given(customerRepository.findByCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(newCustomer));

         // when 
         underTest.registerNewCustomer(request);

         // then
         then(customerRepository).should(never()).save(any());

    }



    @Test
    void itShouldThrowWhenPhoneNumberIsTaken() {

         //Given a phone number and customer
         String phoneNumber="+224758000";
         Customer customer= new Customer(UUID.randomUUID(), "Adam", phoneNumber);
         Customer customer2= new Customer(UUID.randomUUID(), "Hawa", phoneNumber);
     
         // a request
         CustomerRegistrationRequest request= new CustomerRegistrationRequest(customer);
     
         // no customer with phone number passed
         given(customerRepository.findByCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer2));

        
         // when 
         // then
         assertThatThrownBy(() -> underTest.registerNewCustomer(request))
         .isInstanceOf(IllegalStateException.class)
         .hasMessageContaining(String.format("Phone number %s is already take",phoneNumber));
      

        // Finally
       then(customerRepository).should(never()).save(any(Customer.class));

    }


    @Test
    void itShouldSaveNewCustomerWhenIdIsNull(){

        //Given a phone number and customer
        String phoneNumber="+224758000";
        Customer newCustomer= new Customer(null, "Adam", phoneNumber);
    
        // a request
        CustomerRegistrationRequest request= new CustomerRegistrationRequest(newCustomer);
    
        // no customer with phone number passed
        given(customerRepository.findByCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
    
        // when
        underTest.registerNewCustomer(request);

        // then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentsCaptorValue=customerArgumentCaptor.getValue();

       assertThat(customerArgumentsCaptorValue).isEqualToIgnoringGivenFields(newCustomer, "id");
       assertThat(customerArgumentsCaptorValue.getId()).isNotNull();
    }



    
   
    
}
