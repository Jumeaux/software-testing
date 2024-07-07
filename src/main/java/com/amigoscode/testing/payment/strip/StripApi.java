package com.amigoscode.testing.payment.strip;

import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import com.stripe.param.ChargeCreateParams;

@Service
public class StripApi {
    
    public Charge create(ChargeCreateParams params, RequestOptions requestOptions ) throws StripeException{

        return Charge.create(params,requestOptions);
    }

}
