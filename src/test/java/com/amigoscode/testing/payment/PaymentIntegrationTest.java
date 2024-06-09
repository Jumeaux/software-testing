package com.amigoscode.testing.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.Objects;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRegistrationRequest;
import com.amigoscode.testing.payment.twilio.MessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.Message.Status;
import com.twilio.type.PhoneNumber;
@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;  
    
    @Autowired
    private SmsService smsService;

    @Autowired
    private MockMvc mockMvc;
 
    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception{

        //Given a customer
        UUID customerId=UUID.randomUUID();
        Customer customer= new Customer(customerId, "Sidi", "+224625768833");
        CustomerRegistrationRequest customerRequest=new CustomerRegistrationRequest(customer);

        //... Request to register new Customer
        ResultActions customerResultActions= mockMvc.perform(put("/api/customer-registration")
        .contentType(MediaType.APPLICATION_JSON)
        .content(Objects.requireNonNull(objectToJson(customerRequest))));

        //... Payment
        Long paymentId=1L;
        Payment payment= new Payment(paymentId, customerId, 100L, Currency.GNF, "0009xxx", "Payment cours");
       
        //... Payment Request
        PaymentRequest paymentRequest = new PaymentRequest(payment);
       
        //... When payment request is sent
        ResultActions paymentResultAction=mockMvc.perform(post("/api/v1/payment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(Objects.requireNonNull(objectToJson(paymentRequest))));

        //Then
        customerResultActions.andExpect(MockMvcResultMatchers.status().isOk());
        paymentResultAction.andExpect(MockMvcResultMatchers.status().isOk());
        

    
        //...Payment is stored in db
        assertThat(paymentRepository.findById(paymentId))
        .isPresent()
        .hasValueSatisfying(p->{
            assertThat(p).isEqualTo(payment);
        });

       //... sms is delivered
        MessageDTO messagedDto= smsService.fetchMessage("accountSid","sid");
        assertThat(messagedDto.getStatus()).isEqualTo(Status.DELIVERED);


       
    }


    private String objectToJson(Object object){

        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("Failed to convert Object to Json");
            return null;
        }
    }


  
}
