package com.vaccine.notify.notifier.scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vaccine.notify.notifier.model.Vaccine;
import com.vaccine.notify.notifier.service.CowinAPICaller;
import com.vaccine.notify.notifier.service.EmailSenderService;
import com.vaccine.notify.notifier.service.SMSService;









@Component
public class SlotCheckScheduler {

	@Autowired
	private EmailSenderService emailSenderService;
	
	@Autowired
	private CowinAPICaller apiCaller;
	
	@Autowired
	private SMSService smsService;
	
	@Value("${notify.phone.number}")
	private String toPhoneString;
	
	private static final Logger logger = LoggerFactory.getLogger(SlotCheckScheduler.class);
	@Scheduled(cron = "0/30 * * ? * *")
	public void slotAvailabilityCheck() {
		List<Vaccine> result = apiCaller.callCowinAPI();
		if(result.size()>0) {
			String content = apiCaller.generateMail(result);
			String[] phoneList = toPhoneString.split(",");
			for(String phone : phoneList) {
				String smsResult = smsService.sendSMS(phone, content);
				logger.error("SMS send to "+phone+"  -  Status : "+smsResult);
			}
			emailSenderService.sendEmail("ashikpkapril1997@gmail.com", content, "Vaccine Slot Availability");
			logger.error("Email send");
		}
	}
}
