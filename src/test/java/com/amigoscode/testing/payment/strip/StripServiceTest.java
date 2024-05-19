package com.amigoscode.testing.payment.strip;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amigoscode.testing.payment.CardPaymentCharge;
import com.amigoscode.testing.payment.Currency;
import com.amigoscode.testing.payment.strip.StripApi;
import com.amigoscode.testing.payment.strip.StripService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import com.stripe.param.ChargeCreateParams;

public class StripServiceTest {

    private StripService underTest;

    @Mock
    private StripApi stripApi;

    @BeforeEach
    void setUp(){

        MockitoAnnotations.initMocks(this);
        underTest=new StripService(stripApi);
    }

    @Test
    void itShouldChargeCard() throws StripeException {
        //Given
        String sourceCard="1234xxx";
        Long amount=1259L;
        Currency currency=Currency.USD;
        String description="donation";

        //.. create  charge with true paid
        Charge charge = new Charge();
        charge.setPaid(true);
        given(stripApi.create(any(), any())).willReturn(charge);

        //When
       CardPaymentCharge cardPaymentCharge= underTest.chargeCard(sourceCard, amount, currency, description);

       // Then
       ArgumentCaptor<ChargeCreateParams> chargeCreateParamsCaptor=ArgumentCaptor.forClass(ChargeCreateParams.class);
       ArgumentCaptor<RequestOptions> requestOptionsCaptor=ArgumentCaptor.forClass(RequestOptions.class);

       then(stripApi).should().create(chargeCreateParamsCaptor.capture(), requestOptionsCaptor.capture());

       ChargeCreateParams chargeCreateParamsCaptorValue=chargeCreateParamsCaptor.getValue();
       RequestOptions requestOptionsCaptorValue=requestOptionsCaptor.getValue();

       assertThat(chargeCreateParamsCaptorValue.getAmount()).isEqualTo(amount);
       assertThat(chargeCreateParamsCaptorValue.getSource()).isEqualTo(sourceCard);
       assertThat(chargeCreateParamsCaptorValue.getCurrency().toString()).isEqualTo(currency.toString());
       assertThat(chargeCreateParamsCaptorValue.getDescription()).isEqualTo(description);

       assertThat(requestOptionsCaptorValue).isNotNull();
       
       assertThat(cardPaymentCharge).isNotNull();
       assertThat(cardPaymentCharge.isCardDebited()).isTrue();
    }


    /* @Test
    void itShouldThronwWhenAPInotWork() throws StripeException {
        //Given
        String sourceCard="1234xxx";
        BigDecimal amount=new BigDecimal("1259");
        Currency currency=Currency.USD;
        String description="donation";

        //.. create  charge with true paid
      
       // given(stripApi.create(any(), any())).willThrow(IllegalStateException.class);

        //When
        assertThatThrownBy(()->underTest.chargeCard(sourceCard, amount, currency, description))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("error charge card with strip");
      
      
    } */
}
