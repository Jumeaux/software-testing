package com.amigoscode.testing.payment.twilio;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.amigoscode.testing.payment.SmsService;

import com.twilio.rest.api.v2010.account.Message.Status;

@Service
@ConditionalOnProperty(value="twilio.enabled", havingValue="false")
public class MockTwilioService implements SmsService {

    @Override
    public MessageDTO send(String from, String to, String message) {
        
        return new MessageDTO(message,to,"accountSid",Status.DELIVERED,"sid");
    }

    @Override
    public MessageDTO fetchMessage(String pathAccountSid, String pathSid) {
        MessageDTO messageDTO= new MessageDTO();
        messageDTO.setStatus(Status.DELIVERED);
        messageDTO.setAccountSid(pathAccountSid);
        messageDTO.setSid(pathSid);
        return messageDTO;
        
    }  
}
