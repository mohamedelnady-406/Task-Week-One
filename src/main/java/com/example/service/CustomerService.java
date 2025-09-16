package com.example.service;

import com.example.dtos.CustomerDTO;
import com.example.entity.Customer;
import com.example.mapper.CustomerMapper;
import com.example.repository.CustomerRepository;
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
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toDto)
                .toList();
    }

    public HttpResponse<String> save(CustomerDTO customerDTO) {
        customerRepository.save(customerMapper.toEntity(customerDTO));
        return HttpResponse.ok("customer saved successfully !");
    }
    public Optional<CustomerDTO> findById(Long id) {
        return customerRepository.findById(id)
                .map(customer -> CustomerDTO.builder()
                        .name(customer.getName())
                        .email(customer.getEmail())
                        .build()
                ).or(() -> {
                    throw new HttpStatusException(HttpStatus.NOT_FOUND, "Customer not found");
                });
    }
    public void delete(Long id) {
        customerRepository.deleteById(id);
    }
    @Transactional
    public HttpResponse<String> update(Long id, CustomerDTO customerDTO) {
        customerRepository.findById(id)
                .map(existing -> {
                    Customer merged=  customerMapper.updateCustomer(existing, customerDTO);
                    return customerRepository.update(merged);
                })
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return HttpResponse.ok("Updated Successfully!");
    }


}