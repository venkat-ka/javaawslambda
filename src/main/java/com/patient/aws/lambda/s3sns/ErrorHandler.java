package com.patient.aws.lambda.s3sns;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;

public class ErrorHandler {
	public void handler(SNSEvent event) {
		Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
		
	}
}
