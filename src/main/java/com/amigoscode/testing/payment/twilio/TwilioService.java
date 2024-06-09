package com.amigoscode.testing.payment.twilio;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.amigoscode.testing.payment.SmsService;
import com.twilio.rest.api.v2010.account.Message;

@Service
@ConditionalOnProperty(value = "twilio.enabled", havingValue = "true")
public class TwilioService implements SmsService {

    private TwilioApi twilioApi;

    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");

    TwilioService(TwilioApi twilioApi) {
        this.twilioApi = twilioApi;
    }

    @Override
    public MessageDTO send(String to, String from, String body) {

        twilioApi.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = twilioApi.creator(
                new com.twilio.type.PhoneNumber(to),
                new com.twilio.type.PhoneNumber(from),
                body);

        return new MessageDTO(message.getBody(), message.getTo(), message.getAccountSid(), message.getStatus(),
                message.getSid());
    }

    @Override
    public MessageDTO fetchMessage(String accountSid, String sid) {
        twilioApi.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = twilioApi.fetchMessage(accountSid, sid);

        return new MessageDTO(message.getBody(), message.getTo(), message.getAccountSid(), message.getStatus(),
                message.getSid());
    }

}
