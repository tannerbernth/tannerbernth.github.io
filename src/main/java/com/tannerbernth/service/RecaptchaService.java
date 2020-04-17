package com.tannerbernth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

import com.tannerbernth.web.captcha.GoogleResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("recaptchaService")
public class RecaptchaService {

	private final static Logger LOGGER = LoggerFactory.getLogger(RecaptchaService.class);

	private final String secret = "6LfeFp4UAAAAAOthr6dNw76gwtZZcpESzL_i2ZwC";

	@Autowired
	private HttpServletRequest request;

	private Pattern pattern = Pattern.compile("[A-Za-z0-9_-]+");

	public boolean isValidResponse(String response) {
		if (!validInput(response)) {
			return false;
		}
		String uri = "https://www.google.com/recaptcha/api/siteverify?" + 
					 "secret=" + secret + 
					 "&response=" + response + 
					 "&remoteip=" + getIp();
		RestTemplate restTemplate = new RestTemplate();
		GoogleResponse result = restTemplate.getForObject(uri, GoogleResponse.class);
		LOGGER.info(result.toString());
		if (!result.getSuccess()) {
			return false;
		}
		return true;
	}

	public boolean validInput(String response) {
		return response != null && response.length() > 0 && pattern.matcher(response).matches();
	}
	
	private String getIp() {
        final String header = request.getHeader("X-Forwarded-For");
        if (header == null) {
            return request.getRemoteAddr();
        }
        return header.split(",")[0];
    }

}