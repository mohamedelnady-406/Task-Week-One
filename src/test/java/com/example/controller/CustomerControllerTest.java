package com.example.controller;
import com.example.dtos.CustomerDTO;
import com.example.exception.CustomerNotFoundException;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.type.Argument;
import io.micronaut.data.model.Page;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.activemq.ActiveMQContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Property(name = "micronaut.http.client.read-timeout", value = "120s")
class CustomerControllerTest implements TestPropertyProvider {

    @Inject
    @Client("/customer")
    HttpClient client;

    @Inject
    @Client("/")
    HttpClient loginClient;
    @Container
    static MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Container
    final static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("apache/kafka:3.7.0"))
                    .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true");

    @Container
    static ActiveMQContainer activemq = new ActiveMQContainer("apache/activemq-classic:5.18.3");

    @Override
    public @NonNull Map<String, String> getProperties() {
        // ensure both containers are running
        if (!kafka.isRunning()) kafka.start();
        if (!activemq.isRunning()) activemq.start();
        if (!mysql.isRunning()) mysql.start();

        String brokerUrl =
                "tcp://" + activemq.getHost() + ":" + activemq.getMappedPort(61616);

        Map<String,String> props = new HashMap<>();
        props.put("kafka.bootstrap.servers", kafka.getBootstrapServers());
        props.put("micronaut.jms.activemq.classic.connection-string", brokerUrl);
        props.put("datasources.default.url", mysql.getJdbcUrl());
        props.put("datasources.default.username", mysql.getUsername());
        props.put("datasources.default.password", mysql.getPassword());
        return props;
    }

    @Test
    void loginWorks() {
        HttpRequest<?> request = HttpRequest.POST(
                "/auth/login",
                new UsernamePasswordCredentials("user", "password")
        ).contentType(MediaType.APPLICATION_JSON_TYPE);

        try {
            loginClient.toBlocking().exchange(request, Map.class);
        } catch (HttpClientResponseException e) {
            System.out.println("Error body: "
                    + e.getResponse().getBody(String.class).orElse("no body"));
            throw e;
        }
    }
    @Test
    void fullCustomerLifecycle() {
        // Login to get JWT
        HttpResponse<Map<String,Object>> loginResp = loginClient.toBlocking().exchange(
                HttpRequest.POST("/auth/login", new UsernamePasswordCredentials("user", "password"))
                        .contentType(MediaType.APPLICATION_JSON_TYPE),
                Argument.mapOf(String.class, Object.class)
        );
        assertEquals(200, loginResp.getStatus().getCode());

        String token = (String) loginResp.body().get("access_token");
        assertNotNull(token);

        CustomerDTO dto = CustomerDTO.builder()
                .name("Mohamed")
                .email("m@ex.com")
                .build();
        HttpResponse<?> addResp = client.toBlocking().exchange(
                HttpRequest.POST("/add", dto)
                        .bearerAuth(token));
        assertEquals(200, addResp.getStatus().getCode());

        HttpResponse<String> listResp = client.toBlocking().exchange(
                HttpRequest.GET("/all").bearerAuth(token),
                String.class);
        assertTrue(listResp.body().contains("Mohamed"));
    }

    @Test
    void testAddCustomer() {
        HttpResponse<Map<String,Object>> loginResp = loginClient.toBlocking().exchange(
                HttpRequest.POST("/auth/login", new UsernamePasswordCredentials("user", "password"))
                        .contentType(MediaType.APPLICATION_JSON_TYPE),
                Argument.mapOf(String.class, Object.class)
        );
        assertEquals(200, loginResp.getStatus().getCode());

        String token = (String) loginResp.body().get("access_token");
        assertNotNull(token);

        CustomerDTO dto = CustomerDTO.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();
        HttpResponse<?> addResp = client.toBlocking().exchange(
                HttpRequest.POST("/add", dto)
                        .bearerAuth(token));
        assertEquals(200, addResp.getStatus().getCode());
    }


    @Test
   void testGetAllCustomersPaged() {
        HttpResponse<Map<String,Object>> loginResp = loginClient.toBlocking().exchange(
                HttpRequest.POST("/auth/login", new UsernamePasswordCredentials("user", "password"))
                        .contentType(MediaType.APPLICATION_JSON_TYPE),
                Argument.mapOf(String.class, Object.class)
        );
        assertEquals(200, loginResp.getStatus().getCode());

        String token = (String) loginResp.body().get("access_token");
        assertNotNull(token);
        // Add a customer first
        CustomerDTO dto = CustomerDTO.builder()
                .name("Alice")
                .email("alice@example.com")
                .build();
        client.toBlocking().exchange(HttpRequest.POST("/add", dto).bearerAuth(token));

        // Fetch page 0 with size 10
        HttpRequest<?> request = HttpRequest.GET("/all?page=0&size=10").bearerAuth(token);
        HttpResponse<Page> response = client.toBlocking().exchange(
                request,
                Argument.of(Page.class, Argument.of(CustomerDTO.class))
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        @SuppressWarnings("unchecked")
        Page<CustomerDTO> page = (Page<CustomerDTO>) response.body();
        assertNotNull(page);
        List<CustomerDTO> content = page.getContent();
        assertFalse(content.isEmpty());
    }
    @Test
    void testGetCustomerById_Success() {
        HttpResponse<Map<String,Object>> loginResp = loginClient.toBlocking().exchange(
                HttpRequest.POST("/auth/login", new UsernamePasswordCredentials("user", "password"))
                        .contentType(MediaType.APPLICATION_JSON_TYPE),
                Argument.mapOf(String.class, Object.class)
        );
        assertEquals(200, loginResp.getStatus().getCode());

        String token = (String) loginResp.body().get("access_token");
        assertNotNull(token);
        CustomerDTO dto = CustomerDTO.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();

        HttpResponse<String> postResponse = client.toBlocking()
                .exchange(HttpRequest.POST("/add", dto).bearerAuth(token), String.class);

        assertEquals(HttpStatus.OK, postResponse.getStatus());
        HttpResponse<CustomerDTO> response = client.toBlocking().exchange(
                HttpRequest.GET("/" + 1).bearerAuth(token),
                CustomerDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());

    }

    @Test
    void testUpdateCustomer() {
        HttpResponse<Map<String,Object>> loginResp = loginClient.toBlocking().exchange(
                HttpRequest.POST("/auth/login", new UsernamePasswordCredentials("user", "password"))
                        .contentType(MediaType.APPLICATION_JSON_TYPE),
                Argument.mapOf(String.class, Object.class)
        );
        assertEquals(200, loginResp.getStatus().getCode());

        String token = (String) loginResp.body().get("access_token");
        assertNotNull(token);
        // Add one customer
        CustomerDTO dto = CustomerDTO.builder()
                .name("Bob")
                .email("bob@example.com")
                .build();
        client.toBlocking().exchange(HttpRequest.POST("/add", dto).bearerAuth(token), String.class);

        // Update
        CustomerDTO updated = CustomerDTO.builder()
                .name("Bob Updated")
                .email("bob.updated@example.com")
                .build();
        HttpRequest<CustomerDTO> updateRequest = HttpRequest.PUT("/1", updated).bearerAuth(token);
        HttpResponse<String> response = client.toBlocking().exchange(updateRequest, String.class);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteCustomer() {
        HttpResponse<Map<String,Object>> loginResp = loginClient.toBlocking().exchange(
                HttpRequest.POST("/auth/login", new UsernamePasswordCredentials("user", "password"))
                        .contentType(MediaType.APPLICATION_JSON_TYPE),
                Argument.mapOf(String.class, Object.class)
        );
        assertEquals(200, loginResp.getStatus().getCode());

        String token = (String) loginResp.body().get("access_token");
        assertNotNull(token);
        // Add one customer
        CustomerDTO dto = CustomerDTO.builder()
                .name("Charlie")
                .email("charlie@example.com")
                .build();
        client.toBlocking().exchange(HttpRequest.POST("/add", dto).bearerAuth(token), String.class);

        // Delete
        HttpRequest<?> deleteRequest = HttpRequest.DELETE("/1").bearerAuth(token);
        HttpResponse<String> response = client.toBlocking().exchange(deleteRequest, String.class);
        assertEquals(HttpStatus.OK, response.getStatus());
    }
}