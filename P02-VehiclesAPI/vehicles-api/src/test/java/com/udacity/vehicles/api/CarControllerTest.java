package com.udacity.vehicles.api;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() {
        Car car = getCar();
        car.setId(1L);
        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     *
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        mvc.perform(
                post(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     *
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {
        /**
         * DONE: Add a test to check that the `get` method works by calling
         *   the whole list of vehicles. This should utilize the car from `getCar()`
         *   below (the vehicle will be the first in the list).
         */

        /*
            Response body:
            Body = {"links":[{"rel":"self","href":"http://localhost/cars","hreflang":null,"media":null,"title":null,"type":null,"deprecation":null}],
            "content":[{"id":1,"createdAt":null,"modifiedAt":null,"condition":"USED","details":{"body":"sedan","model":"Impala",
            "manufacturer":{"code":101,"name":"Chevrolet"},"numberOfDoors":4,"fuelType":"Gasoline","engine":"3.6L V6","mileage":32280,"modelYear":2018,
            "productionYear":2018,"externalColor":"white"},"location":{"lat":40.73061,"lon":-73.935242,"address":null,"city":null,"state":null,"zip":null},
            "price":null,"links":[{"rel":"self","href":"http://localhost/cars/1","hreflang":null,"media":null,"title":null,"type":null,"deprecation":null},
            {"rel":"cars","href":"http://localhost/cars","hreflang":null,"media":null,"title":null,"type":null,"deprecation":null}]}]}
         */
        mvc.perform(
                MockMvcRequestBuilders.get("/cars")
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print()) // Print MvcResult details to the "standard" output stream.
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[*].id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[*].location.lat").value(40.730610))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[*].location.lon").value(-73.935242))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[*].details.manufacturer.code").value(101))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[*].details.manufacturer.name").value("Chevrolet"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[*].details.model").value("Impala"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[*].details.body").value("sedan"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[*].details.mileage").value(32280))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[*].details.externalColor").value("white"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[*].details.engine").value("3.6L V6"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[*].details.fuelType").value("Gasoline"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[*].details.modelYear").value(2018))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[*].details.productionYear").value(2018))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[*].details.numberOfDoors").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[*].condition").value("USED")
                );
    }

    /**
     * Tests the read operation for a single car by ID.
     *
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {
        /**
         * DONE: Add a test to check that the `get` method works by calling
         *   a vehicle by ID. This should utilize the car from `getCar()` below.
         */

        /*
            response body:
            Body = {"id":1,"createdAt":null,"modifiedAt":null,"condition":"USED","details":{"body":"sedan","model":"Impala",
            "manufacturer":{"code":101,"name":"Chevrolet"},"numberOfDoors":4,"fuelType":"Gasoline","engine":"3.6L V6","mileage":32280,
            "modelYear":2018,"productionYear":2018,"externalColor":"white"},"location":{"lat":40.73061,"lon":-73.935242,"address":null,
            "city":null,"state":null,"zip":null},"price":null,"links":[{"rel":"self","href":"http://localhost/cars/1","hreflang":null,
            "media":null,"title":null,"type":null,"deprecation":null},{"rel":"cars","href":"http://localhost/cars","hreflang":null,
            "media":null,"title":null,"type":null,"deprecation":null}]}
         */
        mvc.perform(
                MockMvcRequestBuilders.get("/cars/1")
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())   // Print the MvcResult details to the standard output stream
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.location.lat").value(40.730610))
                .andExpect(MockMvcResultMatchers.jsonPath("$.location.lon").value(-73.935242))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.manufacturer.code").value(101))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.manufacturer.name").value("Chevrolet"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.model").value("Impala"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.body").value("sedan"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.mileage").value(32280))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.externalColor").value("white"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.engine").value("3.6L V6"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.fuelType").value("Gasoline"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.modelYear").value(2018))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.productionYear").value(2018))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.numberOfDoors").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.condition").value("USED")
                );
    }

    /**
     * Tests the deletion of a single car by ID.
     *
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        /**
         * DONE: Add a test to check whether a vehicle is appropriately deleted
         *   when the `delete` method is called from the Car Controller. This
         *   should utilize the car from `getCar()` below.
         */
        mvc.perform(
                MockMvcRequestBuilders.delete("/cars/1"))
                .andDo(MockMvcResultHandlers.print())   // Print the MvcResult details to the standard output stream
                .andExpect(MockMvcResultMatchers.status().isNoContent()
                );
    }

    /**
     * Tests for successful update of an existing car in the system
     *
     * @throws Exception when car update fails in the system
     */
    @Test
    public void updateCar() throws Exception {
        Car modifiedCar = getCar();

        // update a few attributes
        modifiedCar.setPrice("10000.123");
        Details details = modifiedCar.getDetails();
        details.setEngine("V10");
        details.setModelYear(1949);
        modifiedCar.setDetails(details);

        // mock return the modified car
        given(carService.save(any())).willReturn(modifiedCar);

        /*
         * Response body:
         * Body = {"id":null,"createdAt":null,"modifiedAt":null,"condition":"USED","details":{"body":"sedan",
         * "model":"Impala","manufacturer":{"code":101,"name":"Chevrolet"},"numberOfDoors":4,"fuelType":"Gasoline",
         * "engine":"V10","mileage":32280,"modelYear":1949,"productionYear":2018,"externalColor":"white"},
         * "location":{"lat":40.73061,"lon":-73.935242,"address":null,"city":null,"state":null,"zip":null},
         * "price":"10000.123"}
         */

        mvc.perform(
                MockMvcRequestBuilders.put("/cars/1")
                    .content(json.write(modifiedCar).getJson())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print()) // Print the MvcResult details to the standard output stream
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value("10000.123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.engine").value("V10"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.modelYear").value(1949))
        ;
    }

    /**
     * Tests for successful update of an existing car in the system
     * Includes Address information and Price
     * @throws Exception when car update fails in the system
     */
    @Test
    public void updateCarWithAddressAndPrice() throws Exception {
        Car modifiedCar = getCar();

        // update a few attributes
        modifiedCar.setPrice("10000.123");
        Details details = modifiedCar.getDetails();
        details.setEngine("V10");
        details.setModelYear(1949);
        modifiedCar.setDetails(details);

        // set location info
        Location location = new Location(1d,1d);
        location.setAddress("address A");
        location.setCity("City C");
        location.setState("State S");
        location.setZip("Zip Z");
        modifiedCar.setLocation(location);

        // mock return the modified car
        given(carService.saveWithLocationAndPrice(any())).willReturn(modifiedCar);
        given(mapsClient.getAddress(any())).willReturn(location);

        /*
         * Response body:
         * Body = {"id":null,"createdAt":null,"modifiedAt":null,"condition":"USED","details":{"body":"sedan",
         * "model":"Impala","manufacturer":{"code":101,"name":"Chevrolet"},"numberOfDoors":4,"fuelType":"Gasoline",
         * "engine":"V10","mileage":32280,"modelYear":1949,"productionYear":2018,"externalColor":"white"},
         * "location":{"lat":1.0,"lon":1.0,"address":"address A","city":"City C","state":"State S","zip":"Zip Z"},
         * "price":"10000.123","links":[{"rel":"self","href":"http://localhost/cars/{id}","hreflang":null,"media":null,
         * "title":null,"type":null,"deprecation":null},{"rel":"cars","href":"http://localhost/cars","hreflang":null,
         * "media":null,"title":null,"type":null,"deprecation":null}]}
         */

        mvc.perform(
                MockMvcRequestBuilders.put(new URI("/cars/locationprice/1"))
                        .content(json.write(modifiedCar).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print()) // Print the MvcResult details to the standard output stream
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.location.address").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value("10000.123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.engine").value("V10"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.modelYear").value(1949))
        ;
    }

    /**
     * Creates an example Car object for use in testing.
     *
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }

    private static final String JSON_TO_DESERIALIZE =
            "{" +
                    "\"id\":1," +
                    "\"createdAt\":null," +
                    "\"modifiedAt\":null," +
                    "\"condition\":\"USED\"," +
                    "\"details\":" +
                    "{\"body\":\"sedan\"," +
                    "\"model\":\"Impala\"," +
                    "\"manufacturer\":" +
                    "{\"code\":101," +
                    "\"name\":\"Chevrolet\"}," +
                    "\"numberOfDoors\":4," +
                    "\"fuelType\":\"Gasoline\"," +
                    "\"engine\":\"3.6L V6\"," +
                    "\"mileage\":32280," +
                    "\"modelYear\":2018," +
                    "\"productionYear\":2018," +
                    "\"externalColor\":\"white\"}," +
                    "\"location\":" +
                    "{\"lat\":40.73061," +
                    "\"lon\":-73.935242," +
                    "\"address\":null," +
                    "\"city\":null," +
                    "\"state\":null," +
                    "\"zip\":null}," +
                    "\"price\":null}";
}