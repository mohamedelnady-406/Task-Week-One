package com.example.controllers;
import com.example.dtos.CustomerDTO;
import com.example.services.CustomerService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Controller("/customer")
@RequiredArgsConstructor
public class CustomerController {
    final private CustomerService customerService;
    @Get("/all")
    public List<CustomerDTO> getAll(){
        return customerService.findAll();
    }

    @Get("/{id}") /**
     no need @PathVariable like in Spring**/
    public Optional<CustomerDTO> getCustomer(Long id){
        return customerService.findById(id);
    }
    @Post("/add")
    public HttpResponse<String> addCustomer(@Body CustomerDTO customerDTO){
        return customerService.save(customerDTO);
    }
}
