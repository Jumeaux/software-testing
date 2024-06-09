package com.amigoscode.testing.payment.strip;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.amigoscode.testing.payment.CardPaymentCharge;
import com.amigoscode.testing.payment.CardPaymentCharger;
import com.amigoscode.testing.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import com.stripe.param.ChargeCreateParams;

@Service
@ConditionalOnProperty(
    value = "strip.enabled",
    havingValue = "true"
)
public class StripService implements CardPaymentCharger {

    
    private final StripApi stripApi;

    StripService(StripApi stripApi){
        this.stripApi=stripApi;
    }

    RequestOptions requestOptions = RequestOptions.builder()
    .setApiKey("sk_test_VePHdqKTYQjKNInc7u56JBrQ")
    .build();

    @Override
    public CardPaymentCharge chargeCard(String sourceCard, Long amount, Currency currency, String description) {
       
        ChargeCreateParams params =
        ChargeCreateParams.builder()
            .setAmount( amount.longValue())
            .setCurrency(currency.toString())
            .setSource(sourceCard)
            .setDescription(description)
            .build();

        try {

          Charge charge = stripApi.create(params,requestOptions);
          return new CardPaymentCharge(charge.getPaid())  ;
       
        } catch (StripeException e) {
           throw new IllegalStateException("error charge card with strip");
        }
    }
    
}
