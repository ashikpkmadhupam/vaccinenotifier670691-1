package com.vaccine.notify.notifier.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
	
	
	@GetMapping("/healthcheck")
	public String healthCheck() {
		return "SUCCESS";
	}

}
