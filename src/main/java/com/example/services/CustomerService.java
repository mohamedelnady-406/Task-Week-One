package com.example.services;

import com.example.dtos.CustomerDTO;
import com.example.entities.Customer;
import com.example.repositories.CustomerRepository;
import io.micronaut.http.HttpResponse;

import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

@Singleton
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public List<CustomerDTO> findAll(){
        return  customerRepository.findAll()
                .stream()
                .map(customer -> CustomerDTO.builder()
                        .name(customer.getName())
                        .email(customer.getEmail())
                        .build()
                )
                .toList();
    }
    public HttpResponse<String> save(CustomerDTO customerDTO){
        Customer customer = Customer.builder().
                email(customerDTO.getEmail()).
                name(customerDTO.getName())
                .build();
        customerRepository.save(customer);
        return HttpResponse.ok("customer saved successfully !");
    }
    public Optional<CustomerDTO> findById(Long id) {
        return customerRepository.findById(id)
                .map(customer -> CustomerDTO.builder()
                        .name(customer.getName())
                        .email(customer.getEmail())
                        .build()
                );
    }
    public void delete(Long id) {
        customerRepository.deleteById(id);
    }
    public Customer update(Long id, Customer updated) {
        return customerRepository.findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setEmail(updated.getEmail());
                    return customerRepository.update(existing);
                })
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }


}