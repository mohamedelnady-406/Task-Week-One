package com.example.service;

import com.example.dtos.CustomerDTO;
import com.example.entity.Customer;
import com.example.mapper.CustomerMapper;
import com.example.repository.CustomerRepositoryFacade;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Singleton
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepositoryFacade customerRepositoryFacade;
    private final CustomerMapper customerMapper;

    public Page<CustomerDTO> getCustomers(Pageable pageable) {
        return customerRepositoryFacade.findAll(pageable)
                .map(customerMapper::toDto);
    }

    public HttpResponse<?> addCustomer(CustomerDTO customerDTO) {
        customerRepositoryFacade.save(customerMapper.toEntity(customerDTO));
        return HttpResponse.ok("Customer registered!");
    }
    public Optional<CustomerDTO> findById(Long id) {
        return customerRepositoryFacade.findById(id)
                .map(customer -> CustomerDTO.builder()
                        .name(customer.getName())
                        .email(customer.getEmail())
                        .build()
                ).or(() -> {
                    throw new HttpStatusException(HttpStatus.NOT_FOUND, "Customer not found");
                });
    }
    public void delete(Long id) {

        customerRepositoryFacade.deleteById(id);
    }
    @Transactional
    public HttpResponse<?> updateCustomer(Long id, CustomerDTO customerDTO) {
        customerRepositoryFacade.findById(id)
                .map(existing -> {
                    Customer merged=  customerMapper.updateCustomer(existing, customerDTO);
                    return customerRepositoryFacade.update(merged);
                })
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return HttpResponse.ok("Updated!");
    }


}