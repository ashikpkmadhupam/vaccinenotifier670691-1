package com.vaccine.notify.notifier.service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TelegramBotService {

	@Value("${notify.telegram.url}")
	private String url;
	
	@Value("${notify.telegram.token}")
	private String token;
	
	@Value("${notify.telegram.chatid}")
	private String chatId;
	
	
	private static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);
	
	public void sendTelegramMessage(String msg) {
		
		
		
		HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_2)
                .build();
 
        UriBuilder builder = UriBuilder
                .fromUri("https://api.telegram.org")
                .path("/{token}/sendMessage")
                .queryParam("chat_id", chatId)
                .queryParam("text", msg);
 
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(builder.build("bot" + token))
                .timeout(Duration.ofSeconds(5))
                .build();
 
        HttpResponse<String> response;
		try {
			response = client
			  .send(request, HttpResponse.BodyHandlers.ofString());
			
			logger.error("Telegram Message send with status : "+response.statusCode());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage()); 
		} 
		
	}
}
