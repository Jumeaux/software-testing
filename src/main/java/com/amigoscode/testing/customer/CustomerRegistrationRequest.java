package com.amigoscode.testing.customer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerRegistrationRequest {
    
    private final Customer customer;


    CustomerRegistrationRequest( @JsonProperty("customer") Customer customer){
        this.customer=customer;
    }


    public Customer getCustomer(){
        return customer;
    }


}
