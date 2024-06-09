package com.amigoscode.testing.payment;

import com.amigoscode.testing.payment.twilio.MessageDTO;

public interface SmsService {
    
    MessageDTO send( String to,String from,String body);

   MessageDTO fetchMessage(String accountSid, String sid);
}
