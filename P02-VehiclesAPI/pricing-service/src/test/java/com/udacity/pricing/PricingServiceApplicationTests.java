package com.udacity.pricing;

import com.udacity.pricing.domain.price.Price;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PricingServiceApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	// testing the service as a REST API with controller and service classes
	// - using randomly created prices
	@Test
	public void generatePriceByVehicleId() {
		Price price = restTemplate.getForObject("http://localhost:" + port + "/services/price?vehicleId=1", Price.class);
		assert(price.getPrice()!=null);
		System.out.println("vehicleId=1 -> price="+price.getPrice());
	}

	// testing the service as a microservice with only entity and repo classes
	// - using prices set in the database h2
	@Test
	public void getPriceByVehicleId() {
		ResponseEntity<Price> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/prices/1", Price.class);
		assert(responseEntity.getBody().getPrice()!=null);
		assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.OK));
		System.out.println("vehicleId=1 -> price="+responseEntity.getBody().getPrice());
	}
}
