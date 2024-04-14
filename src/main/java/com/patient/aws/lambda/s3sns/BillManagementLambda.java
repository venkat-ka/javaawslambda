package com.patient.aws.lambda.s3sns;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillManagementLambda {
	ObjectMapper objectMapper = new ObjectMapper();
	public void handle(SNSEvent event) {
		event.getRecords().forEach(snsRecord->{
			try {
				PatientCheckoutEvent patChkOutEvnt = objectMapper.readValue(snsRecord.getSNS().getMessage(), PatientCheckoutEvent.class);
				System.out.println(patChkOutEvnt);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}
