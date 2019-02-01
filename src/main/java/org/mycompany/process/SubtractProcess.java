package org.mycompany.process;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.mycompany.dataObjects.PayloadObject;

public class SubtractProcess implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		PayloadObject payload = exchange.getIn().getBody(PayloadObject.class);
		int balance = payload.getSubAccount() - payload.getAmount();
		payload.setSubAccount(balance);
		exchange.getIn().setBody(payload);	
	}

}
