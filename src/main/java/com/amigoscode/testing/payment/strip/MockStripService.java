package com.amigoscode.testing.payment.strip;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.amigoscode.testing.payment.CardPaymentCharge;
import com.amigoscode.testing.payment.CardPaymentCharger;
import com.amigoscode.testing.payment.Currency;

@Service
@ConditionalOnProperty(
    value = "strip.enabled",
    havingValue = "false"
)
public class MockStripService  implements CardPaymentCharger{

    @Override
    public CardPaymentCharge chargeCard(String sourceCard, Long amount, Currency currency, String description) {

        return new CardPaymentCharge(true);
    }
    
}
