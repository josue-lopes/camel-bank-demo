package org.mycompany.routes;

import javax.xml.bind.JAXBContext;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.mycompany.dataObjects.PayloadObject;
import org.mycompany.process.AddProcess;
import org.mycompany.process.SubtractProcess;

public class MainRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		// REST set up
		restConfiguration()
		.component("servlet").port(8081)
		.bindingMode(RestBindingMode.xml);
		
		// REST entry point
		rest("/camel")
		.consumes("application/xml")
		.produces("application/xml")
		.post("/request").route().id("RESTRoute")  
		.to("direct:processRoute");
		
		// set up data format for XML conversion
		JaxbDataFormat payloadMarshall = new JaxbDataFormat();
		payloadMarshall.setContext(JAXBContext.newInstance(PayloadObject.class));
		
		// set up processors
		AddProcess addProc = new AddProcess();
		SubtractProcess subProc = new SubtractProcess();
		
		// Initial routing and header creation
		//from("file:camelData/input")
		from("direct:processRoute").routeId("CBRRoute")
		.setHeader("action", xpath("payload/action/@value"))
		.log("${header.action} is the current action.")
		.choice()
			.when(header("action").isEqualTo("deposit"))
				.to("direct:depositEndpoint")
			.when(header("action").isEqualTo("transfer"))
				.to("direct:transferEndpoint");
		
		// Set up deposit routing slip
		from("direct:depositEndpoint").routeId("DepositRoute")
		.setHeader("routing", constant("direct:addEndpoint,direct:finalEndpoint"))
		.log("${header.routing} is the current routing slip.")
		.to("direct:routingEndpoint");
		
		// Set up transfer routing slip
		from("direct:transferEndpoint").routeId("TransferRoute")
		.setHeader("routing", constant("direct:subtractEndpoint,direct:addEndpoint,direct:finalEndpoint"))
		.log("${header.routing} is the current routing slip.")
		.to("direct:routingEndpoint");
		
		// Routing slip to process
		from("direct:routingEndpoint").routeId("RoutingSlipRoute")
		.threads(20)	// extra threads for marshaling to have more resources
		.unmarshal(payloadMarshall)
		.routingSlip(header("routing"));
		
		// subtract money from account
		from("direct:subtractEndpoint").routeId("SubtractRoute")
		.process(subProc)
		.log("At subtractEndpoint");
		
		// add money from account
		from("direct:addEndpoint").routeId("AddRoute")
		.process(addProc)
		.log("At addEndpoint");
		
		// marshal back to xml 
		from("direct:finalEndpoint").routeId("OutputRoute")
		.threads(20)	// extra threads for marshaling to have more resources
		.marshal(payloadMarshall)
		.to("file:camelData/output");
	}
}
