package it.io.openliberty.guides.hateoas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class EndpointIT {

	private String port;
	private String baseURL;
	private Client client;
	
	private final String SYSTEM_PROPERTIES = "system/properties" ;
	private final String INVENTORY_HOSTS = "inventory/hosts" ;
	
	@BeforeEach
	public void setup() {
		port = System.getProperty("http.port");
		baseURL = "http://localhost:" + port + "/" ;
		client = ClientBuilder.newClient();
		client.register(JsrJsonpProvider.class);
	}
	
	@AfterEach
	public void teardown() {
		client.close();
	}
	
	@Test
	@Order(1)
	public void testLinkForInventoryContents() {
		Response response = getResponse(baseURL+INVENTORY_HOSTS);
		assertEquals( 200 , response.getStatus() , "Incorrect response code from " + baseURL );
		JsonObject systems = response.readEntity(JsonObject.class);
		String excepted , actual;
		boolean isFound = false;
		
		if( !systems.isNull("*") ) {
			isFound = true ;
			JsonArray links = systems.getJsonArray("*");
			
			excepted = baseURL + INVENTORY_HOSTS + "/*" ;
			actual = links.getJsonObject(0).getString("href");
			
			assertEquals( excepted , actual , "Incorrect href." );
			
			excepted = "self" ;
			actual = links.getJsonObject(0).getString("rel");
			assertEquals( excepted , actual , "Incorrent rel." );
			
		}
		
		assertTrue(isFound,"Could not find system with hostname *");
		
		response.close();
		
	}
	
	@Test
	@Order(2)
	public void testLinksForSystem() {
		
		visitLocalhost();
		
		Response response = getResponse(baseURL+INVENTORY_HOSTS);
		assertEquals( 200 , response.getStatus() , "Incorrect response code from " + baseURL );
		
		JsonObject systems = response.readEntity(JsonObject.class);
		
		String excepted , actual;
		boolean isHostnameFound = false;
		
		if( !systems.isNull("localhost") ) {
			isHostnameFound = true ;
			JsonArray links = systems.getJsonArray("localhost");
			
			excepted = baseURL + INVENTORY_HOSTS + "/localhost" ;
			actual = links.getJsonObject(0).getString("href");
			assertEquals( excepted , actual , "Incorrect href." );
			
			excepted = "self" ;
			actual = links.getJsonObject(0).getString("rel");
			assertEquals( excepted , actual , "Incorrect rel.");
			
			excepted = baseURL + SYSTEM_PROPERTIES ;
			actual = links.getJsonObject(1).getString("href");
			assertEquals(excepted, actual , "Incorrect href.");

			excepted = "properties" ;
			actual = links.getJsonObject(1).getString("rel");
			assertEquals( excepted, actual , "Incorrect rel." );
			
		}
		
		assertTrue( isHostnameFound , "Could not find system with hostname * ." );
		response.close();
		
	}
	
	private Response getResponse( String url ) {
		return client.target( url ).request().get();
	}
	
	private void visitLocalhost() {
		Response response = getResponse(baseURL+SYSTEM_PROPERTIES);
		assertEquals( 200 , response.getStatus() , "Incorrect response code from " + baseURL );
		response.close();
		Response targetResponse = client.target( baseURL + INVENTORY_HOSTS + "/localhost" ).request().get();
		targetResponse.close();
	}
	
}
