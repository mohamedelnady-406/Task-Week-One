package com.example.controller;

import com.example.dtos.CustomerDTO;
import io.micronaut.core.type.Argument;
import io.micronaut.data.model.Page;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class CustomerControllerTest {

    @Inject
    @Client("/customer")
    HttpClient client;

    @Test
    void testAddCustomer() {
        CustomerDTO dto = CustomerDTO.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();

        HttpRequest<CustomerDTO> request = HttpRequest.POST("/add", dto);
        HttpResponse<?> response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.OK, response.getStatus());
    }


    @Test
   void testGetAllCustomersPaged() {
        // Add a customer first
        CustomerDTO dto = CustomerDTO.builder()
                .name("Alice")
                .email("alice@example.com")
                .build();
        client.toBlocking().exchange(HttpRequest.POST("/add", dto));

        // Fetch page 0 with size 10
        HttpRequest<?> request = HttpRequest.GET("/all?page=0&size=10");
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
    void testGetCustomerByIdNotFound() {
        HttpRequest<?> request = HttpRequest.GET("/99999"); // non-existent ID

        HttpClientResponseException ex = assertThrows(
                io.micronaut.http.client.exceptions.HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, String.class)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());

        // Check message body instead of exception message
        String body = ex.getResponse().getBody(String.class).orElse("");
        assertTrue(body.contains("Customer not found"));
    }
    @Test
    void testGetCustomerById_Success() {
        CustomerDTO dto = CustomerDTO.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();

        HttpResponse<String> postResponse = client.toBlocking()
                .exchange(HttpRequest.POST("/add", dto), String.class);

        assertEquals(HttpStatus.OK, postResponse.getStatus());
        HttpResponse<CustomerDTO> response = client.toBlocking().exchange(
                HttpRequest.GET("/" + 1),
                CustomerDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());

    }

    @Test
    void testUpdateCustomer() {
        // Add one customer
        CustomerDTO dto = CustomerDTO.builder()
                .name("Bob")
                .email("bob@example.com")
                .build();
        client.toBlocking().exchange(HttpRequest.POST("/add", dto), String.class);

        // Update
        CustomerDTO updated = CustomerDTO.builder()
                .name("Bob Updated")
                .email("bob.updated@example.com")
                .build();
        HttpRequest<CustomerDTO> updateRequest = HttpRequest.PUT("/1", updated);
        HttpResponse<String> response = client.toBlocking().exchange(updateRequest, String.class);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteCustomer() {
        // Add one customer
        CustomerDTO dto = CustomerDTO.builder()
                .name("Charlie")
                .email("charlie@example.com")
                .build();
        client.toBlocking().exchange(HttpRequest.POST("/add", dto), String.class);

        // Delete
        HttpRequest<?> deleteRequest = HttpRequest.DELETE("/1");
        HttpResponse<String> response = client.toBlocking().exchange(deleteRequest, String.class);
        assertEquals(HttpStatus.OK, response.getStatus());
    }
}