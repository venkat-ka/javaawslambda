package com.patient.aws.lambda.s3sns;


import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PatientCheckoutLambda {
	private static final String PATIENT_CHECKOUT_TOPIC = System.getenv("PATIENT_CHECKOUT_TOPIC");
	private final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
	private final ObjectMapper objMp = new ObjectMapper();
	private final AmazonSNS sns = AmazonSNSClientBuilder.defaultClient();
	public void handler(S3Event event, Context context) {
		Logger logger = LoggerFactory.getLogger(PatientCheckoutLambda.class);
		 
		event.getRecords().forEach(record->{
			S3ObjectInputStream s3Inputstream =  s3.getObject(
					record.getS3().getBucket().getName(),
					record.getS3().getObject().getKey()
					).getObjectContent();
			try {
				logger.info("Reading data from S3");
				List<PatientCheckoutEvent> patLst = Arrays.asList(objMp.readValue(s3Inputstream, PatientCheckoutEvent[].class));
				logger.info(patLst.toString());
				s3Inputstream.close();
				logger.info("Message being published to SNS");	
				publishMessageSNS(patLst);
				System.out.println(patLst);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				
				logger.error("Excetion is"+e);
				throw new RuntimeException("Error while process S3 event", e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		});
	}
	private void publishMessageSNS(List<PatientCheckoutEvent> patLst) {
		patLst.forEach(checkoutEvent->{
			try {
				sns.publish(PATIENT_CHECKOUT_TOPIC, objMp.writeValueAsString(checkoutEvent));
				
			} catch (JsonProcessingException e) {
				
				e.printStackTrace();
			}	
		});
	}
}
