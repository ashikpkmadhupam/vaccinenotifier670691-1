package com.vaccine.notify.notifier.service;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaccine.notify.notifier.model.Root;
import com.vaccine.notify.notifier.model.Session;
import com.vaccine.notify.notifier.model.Vaccine;


@Service
public class CowinAPICaller {

	@Value("${cowin.get.api.address}")
	private String url;
	
	@Value("${notify.pincode}")
	private String pinCode;
	
	private static final Logger logger = LoggerFactory.getLogger(CowinAPICaller.class);
	private final CloseableHttpClient httpClient = HttpClients.createDefault();
	
	public List<Vaccine> callCowinAPI() {
		
		String date = getDate();
		String URL=""+url;
		URL = URL+"?pincode="+pinCode+"&date="+date;
		logger.error("Calling : "+URL);
		HttpGet request = new HttpGet(URL);
		request.addHeader("authority", "cdn-api.co-vin.in");
        request.addHeader("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"90\", \"Google Chrome\";v=\"90\"");
        request.addHeader("accept", "application/json, text/plain, */*");
        request.addHeader("sec-ch-ua-mobile", "?0");
        request.addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36");
        request.addHeader("origin", "https://www.cowin.gov.in");
        request.addHeader("sec-fetch-mode", "cors");
        request.addHeader("sec-fetch-dest", "empty");
        request.addHeader("referer", "https://www.cowin.gov.in/");
        request.addHeader("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
        request.addHeader("if-none-match", "W/\"755-kSDAGwS0dhuJu/VuZ3UJpZ2STnc\"");
        
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<Vaccine> avalable = new ArrayList<Vaccine>();
		try(CloseableHttpResponse response = httpClient.execute(request)) {
			
			HttpEntity entity = response.getEntity();
	        if (entity != null) {
                
                String result = EntityUtils.toString(entity);
                Root root = objectMapper.readValue(result, Root.class);
                for(int i = 0; i < root.centers.size(); i++){
                    List<Session> sessions = root.centers.get(i).getSessions();
                    for(int j = 0; j < sessions.size(); j++){
                        Session session = sessions.get(j);
                        if(session.available_capacity > 0){
                            Vaccine v= new Vaccine();
                            v.setCenter(root.centers.get(i).getName());
                            v.setDate(session.getDate());
                            v.setCount(""+session.getAvailable_capacity());
                            v.setType(session.getVaccine());
                            avalable.add(v);
                        }
                    }
                }

                
            }
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return avalable;
	}
	
	private String getDate() {
		
		final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
		String date = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(now);
		return date;
	}
	
	public String generateMail(List<Vaccine> vList) {
		
		String mailContent="";
		for(Vaccine v:vList) {
			mailContent=mailContent+"----------------------------------------------------------------\n";
			mailContent=mailContent+v.getCenter()+"\n";
			mailContent=mailContent+v.getDate()+"\n";
			mailContent=mailContent+v.getType()+"\n";
			mailContent=mailContent+v.getCount()+"\n";
			mailContent=mailContent+"----------------------------------------------------------------\n";
		}
		
		return mailContent;
	}
}
