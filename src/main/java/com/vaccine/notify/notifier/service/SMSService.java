package com.vaccine.notify.notifier.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class SMSService {

	@Value("${notify.twilio.account.sid}")
	private String accountSID;
	
	@Value("${notify.twilio.account.authcode}")
	private String authToken;
	
	@Value("${notify.twilio.from.number}")
	private String twilioPhoneNumber;
	
	public String sendSMS(String toNumber, String msg) {
		
		Twilio.init(accountSID, authToken);
		Message message = Message.creator(
		    new PhoneNumber(toNumber),
		    new PhoneNumber(twilioPhoneNumber),
		    msg)
		.create();
		
		return ""+message.getStatus();
	}
}
