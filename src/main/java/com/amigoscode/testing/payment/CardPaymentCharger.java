package com.amigoscode.testing.payment;


public interface CardPaymentCharger {
    

    CardPaymentCharge chargeCard(String sourceCard, Long amount, Currency currency, String description);
}
