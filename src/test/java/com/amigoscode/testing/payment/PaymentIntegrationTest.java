package com.amigoscode.testing.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


import java.util.Objects;
import java.util.UUID;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRegistrationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {

      
    @Autowired
    private MockMvc mockMvc;
 
    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception{

        //Given a customer
        UUID customerId=UUID.randomUUID();
        Customer customer= new Customer(customerId, "Sidi", "00224");
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


        //... Payment is stored in db
        MvcResult result= mockMvc.perform(get("/api/v1/payment/"+customerId))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

        String json= result.getResponse().getContentAsString();
        List<Payment> payments= new ObjectMapper().readValue(json, TypeFactory.defaultInstance().constructCollectionType(List.class,   Payment.class));

        assertEquals(payments.size(),1);
        assertThat(payments.get(0).getPaymentId()).isEqualTo(paymentId);

      /*   assertThat(paymentRepository.findById(paymentId))
        .isPresent()
        .hasValueSatisfying(p->{
            assertThat(p).isEqualTo(payment);
        });
        */
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
