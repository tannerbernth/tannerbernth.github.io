package com.tannerbernth.web.captcha;

import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "success", "challenge_ts", "hostname", "error-codes" })
public class GoogleResponse {

	@JsonProperty("success")
	public boolean success;

	@JsonProperty("challenge_ts")
	public String challengeTs;

	@JsonProperty("hostname")
	public String hostname;

	@JsonProperty("error-codes")
	public String[] errorCodes;

	@JsonProperty("success")
	public boolean getSuccess() {
		return success;
	}

	@JsonProperty("success")
	public void setSuccess(boolean success) {
		this.success = success;
	}

	@JsonProperty("challenge_ts")
	public String getChallengeTs() {
		return challengeTs;
	}

	@JsonProperty("challenge_ts")
	public void setChallengeTs(String challengeTs) {
		this.challengeTs = challengeTs;
	}

	@JsonProperty("hostname")
	public String getHostname() {
		return hostname;
	}

	@JsonProperty("hostname")
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@JsonProperty("error-codes")
	public String[] getErrorCodes() {
		return errorCodes;
	}

	@JsonProperty("error-codes")
	public void setErrorCodes(String[] errorCodes) {
		this.errorCodes = errorCodes;
	} 

	@Override
	public String toString() {
		return "Google Response: \n{\n" + 
			   "\"success\" : " + success + 
			   ",\n\"challenge_ts\" : " + challengeTs + 
			   ",\n\"hostname\" : " + hostname + "\n}";// + 
			   //",error-codes : [" + String.join(",",errorCodes) + "]\n}"; 
	}
}