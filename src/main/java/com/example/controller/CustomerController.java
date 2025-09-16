package com.example.controller;
import com.example.dtos.CustomerDTO;
import com.example.service.CustomerService;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
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
    public HttpResponse<List<CustomerDTO>> getAll() {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        return HttpResponse.ok(customers);
    }

    @Get("/{id}")
    public HttpResponse<CustomerDTO> getCustomer(@PathVariable Long id) {
        return customerService.findById(id)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.status(HttpStatus.NOT_FOUND));
    }
    @Post("/add")
    public HttpResponse<?> addCustomer(@Body CustomerDTO customerDTO){
        return customerService.addCustomer(customerDTO);
    }
    @Delete("/{id}")
    public HttpResponse<?> delete(@PathVariable Long id) {
        customerService.delete(id);
        return HttpResponse.ok();
    }
    @Put("/{id}")
    public HttpResponse<?> update(Long id, @Body @Valid CustomerDTO customerDto) {
        return customerService.updateCustomer(id,customerDto);
    }

}
