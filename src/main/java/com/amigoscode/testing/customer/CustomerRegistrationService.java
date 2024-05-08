package com.amigoscode.testing.customer;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class CustomerRegistrationService {
    
    private final CustomerRepository customerRepository;

    CustomerRegistrationService(CustomerRepository customerRepository){
        this.customerRepository=customerRepository;
    }
    
    public void registerNewCustomer(CustomerRegistrationRequest request){

        String phoneNumber=request.getCustomer().getPhoneNumber();

       Optional<Customer> optionalCustomer= customerRepository.findByCustomerByPhoneNumber(phoneNumber);

       if (optionalCustomer.isPresent()) {
            Customer customer=optionalCustomer.get();
            if(customer.getName().equals(request.getCustomer().getName())) {
                return;
            }
            throw new IllegalStateException(String.format("Phone number %s is already take",phoneNumber));
       }

       if (request.getCustomer().getId() == null) {
            request.getCustomer().setId(UUID.randomUUID());
       }
       customerRepository.save(request.getCustomer());

    }
}
