package com.example.controller;
import com.example.dtos.CustomerDTO;
import com.example.service.CustomerService;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@Controller("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final  CustomerService customerService;
    @Get("/all")
    public HttpResponse<Page<CustomerDTO>> getAllCustomers(Pageable pageable) {
        Page<CustomerDTO> customers = customerService.getCustomers(pageable);
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
    public HttpResponse<String> delete(Long id) {
        customerService.deleteCustomer(id);
        return HttpResponse.ok("Deleted!");
    }
    @Put("/{id}")
    public HttpResponse<?> update(Long id, @Body @Valid CustomerDTO customerDto) {
        return customerService.updateCustomer(id,customerDto);
    }

}
