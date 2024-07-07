package com.amigoscode.testing.payment;

import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("/api/v1")

public class PaymentController {
   
    private PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService=paymentService;
    }
    
    @PostMapping("/payment")
    public void makePayment( @RequestBody PaymentRequest paymentRequest) {
        paymentService.chargeCard(paymentRequest.getPayment().getCustomerId(),paymentRequest);
    }

    @GetMapping("/payment/{customerId}")
    public List<Payment> getPaymentByCustomer(@PathVariable UUID customerId){

        List<Payment> payments= paymentService.getPaymentByCustomer(customerId);

        return payments;
    }
}
