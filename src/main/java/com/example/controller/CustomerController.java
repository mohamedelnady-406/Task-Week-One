package com.example.controller;
import com.example.dtos.CustomerDTO;
import com.example.service.CustomerService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Controller("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final  CustomerService customerService;
    @Get("/all")
    public List<CustomerDTO> getAll(){
        return customerService.getAllCustomers();
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
    @Delete("/{id}")
    public HttpResponse<String> delete(Long id) {
        customerService.delete(id);
        return HttpResponse.ok("Deleted Successfully!");
    }
    @Put("/{id}")
    public HttpResponse<String> update(Long id, @Body @Valid CustomerDTO customerDto) {
        return customerService.update(id,customerDto);
    }

}
