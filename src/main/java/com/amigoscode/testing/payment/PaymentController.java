package com.amigoscode.testing.payment;

import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/payment")

public class PaymentController {
   
    private PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService=paymentService;
    }
    
    @RequestMapping
    public void makePayment( @RequestBody PaymentRequest paymentRequest) {
        paymentService.chargeCard(paymentRequest.getPayment().getCustomerId(),paymentRequest);
    }
}
