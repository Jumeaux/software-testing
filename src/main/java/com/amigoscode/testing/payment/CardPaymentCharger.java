package com.amigoscode.testing.payment;

import java.math.BigDecimal;

public interface CardPaymentCharger {
    

    CardPaymentCharge cardCharge(String sourceCard, BigDecimal amount, Currency currency, String description);
}
