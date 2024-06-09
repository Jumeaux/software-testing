package com.amigoscode.testing.payment.twilio;


import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import com.twilio.type.PhoneNumber;

@Service
public class TwilioApi {
    
    Message creator(PhoneNumber to, PhoneNumber from,String body){
      
    return Message.creator( to, from, body).create();
    
  }

  void init(String accountSid, String authToken){
        Twilio.init(accountSid, authToken);
  }


  Message fetchMessage(String accountSid, String sid){
    return Message.fetcher(accountSid,sid).fetch();
  }


  Message convertToMessage(MessageDTO messageDTO){

    String json= objectTojson(messageDTO);
    Message message =  Message.fromJson(json, new ObjectMapper());
    return message;
  }

 

  String objectTojson(Object object){

        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }



    


}
