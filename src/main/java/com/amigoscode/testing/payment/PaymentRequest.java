package com.amigoscode.testing.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentRequest {

    private final Payment payment;

    PaymentRequest(@JsonProperty("payment") Payment payment){
        this.payment=payment;
    }

    public Payment getPayment() {
        return payment;
    }


    
    
}
