package com.example.services;

import com.example.dtos.CustomerDTO;
import com.example.entities.Customer;
import com.example.repositories.CustomerRepository;
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
                    existing.setName(customerDTO.getName());
                    existing.setEmail(customerDTO.getEmail());
                    return customerRepository.update(existing);
                })
                .orElseThrow(() -> new RuntimeException("Customer not found"));
         return  HttpResponse.ok("Updated Successfully!");
    }


}