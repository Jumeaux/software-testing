package com.amigoscode.testing.payment.twilio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.twilio.rest.api.v2010.account.Message.Status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioServiceTest {

    private TwilioService underTest;

    @Mock
    private TwilioApi twilioApi;

  
    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        underTest= new TwilioService(twilioApi);
    }
    @Test
    void itShouldSendSmsSuccessfully() {

        // Given
        String from ="+224625437133";
        String to ="+224625437132";
        String body ="Hello word"; 


        Message message =convertToMessage( new MessageDTO(body, to, "accountSid", Status.DELIVERED, null));

       
        doNothing().when(twilioApi).init(any(), any());
        given(twilioApi.creator( any(),  any(),  any())).willReturn(message);

        // When
        MessageDTO messsageDto= underTest.send(to,from, body);

        //Then
        ArgumentCaptor<PhoneNumber> phoneNumberToCaptor=ArgumentCaptor.forClass(PhoneNumber.class);
        ArgumentCaptor<PhoneNumber> phoneNumberFromCaptor=ArgumentCaptor.forClass(PhoneNumber.class);
        ArgumentCaptor<String> bodyCaptor=ArgumentCaptor.forClass(String.class);

        then(twilioApi).should().creator(phoneNumberToCaptor.capture(), phoneNumberFromCaptor.capture(), bodyCaptor.capture());
       
        PhoneNumber phoneNumberToCaptorValue=phoneNumberToCaptor.getValue();
        PhoneNumber phoneNumberFromCaptorValue=phoneNumberFromCaptor.getValue();
        String bodyCaptorValue=bodyCaptor.getValue();

        assertThat(phoneNumberToCaptorValue.toString()).isEqualTo(to);
        assertThat(phoneNumberFromCaptorValue.toString()).isEqualTo(from);
        assertThat(bodyCaptorValue.toString()).isEqualTo(body);

        assertThat(messsageDto).isNotNull();
        assertThat(messsageDto.getStatus()).isEqualTo(Status.DELIVERED);
    }



    @Test
    void itShouldFetchSms() {
        // Given
        String accountSid="accountSid";
        String sid="sid";
        
        doNothing().when(twilioApi).init(any(), any());

        MessageDTO messageDTO= new MessageDTO("message body", "+224626437100", accountSid, Status.SENT, sid);
        Message message =convertToMessage(messageDTO);
        given(twilioApi.fetchMessage(any(), any())).willReturn(message);

        // When
        MessageDTO messageFetched= underTest.fetchMessage(accountSid,sid);

        //Then
        assertThat(messageFetched).isNotNull();

        assertThat(messageFetched.getAccountSid()).isNotNull();
        assertThat(messageFetched.getBody()).isNotNull();
        assertThat(messageFetched.getSid()).isNotNull();
        assertThat(messageFetched.getStatus()).isNotNull();
        assertThat(messageFetched.getTo()).isNotNull();

        assertThat(messageFetched.getSid()).isEqualTo(sid);
        assertThat(messageFetched.getAccountSid()).isEqualTo(accountSid);
    }



    public Message convertToMessage(MessageDTO messageDTO) {
        
        try {

            String json= new ObjectMapper().writeValueAsString(messageDTO);
            Message message= Message.fromJson(json, new ObjectMapper());
            return message;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

    }
}
