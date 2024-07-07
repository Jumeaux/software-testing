package com.amigoscode.testing.customer;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;



@RestController
@RequestMapping("/api/customer-registration")
public class CustomerRegistrationController {
    private final CustomerRegistrationService customerRegistrationService;

  public CustomerRegistrationController(CustomerRegistrationService customerRegistrationService){
    this.customerRegistrationService=customerRegistrationService;
  }

    @PutMapping
    public void registerNewCustomer(
      @Valid @RequestBody CustomerRegistrationRequest customerRegistrationRequest){


        customerRegistrationService.registerNewCustomer(customerRegistrationRequest);
    }
}
