package com.vaccine.notify.notifier.scheduler;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vaccine.notify.notifier.model.Vaccine;
import com.vaccine.notify.notifier.service.CowinAPICaller;
import com.vaccine.notify.notifier.service.TelegramBotService;









@Component
public class SlotCheckScheduler {
	
	@Autowired
	private CowinAPICaller apiCaller;
	
	@Autowired
	private TelegramBotService telegramBotService;
	
	@Scheduled(cron = "0/20 * * ? * *")
	public void slotAvailabilityCheck() {
		List<Vaccine> result = apiCaller.callCowinAPI();
		if(result.size()>0) {
			String content = apiCaller.generateMail(result);
			telegramBotService.sendTelegramMessage(content);
		}
	}
}
