package com.example.service;

import com.example.dtos.CustomerDTO;
import com.example.entity.Customer;
import com.example.exception.CustomerNotFoundException;
import com.example.jms.MessageProducer;
import com.example.kafka.CustomerEventProducer;
import com.example.mapper.CustomerMapper;
import com.example.repository.CustomerRepositoryFacade;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Singleton
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepositoryFacade customerRepositoryFacade;
    private final CustomerMapper customerMapper;
    private final CustomerEventProducer producer;
    private final MessageProducer messageProducer;

    public Page<CustomerDTO> getCustomers(Pageable pageable) {
        return customerRepositoryFacade.findAll(pageable)
                .map(customerMapper::toDto);
    }

    @Transactional
    public HttpResponse<?> addCustomer(CustomerDTO customerDTO) {
        Customer cst = customerMapper.toEntity(customerDTO);
        customerRepositoryFacade.save(cst);
        producer.sendCustomerCreated("New Customer add: "+cst.toString());
        messageProducer.send(cst.toString());
        return HttpResponse.ok("Customer registered!");
    }
    public Optional<CustomerDTO> findById(Long id) {
        return customerRepositoryFacade.findById(id)
                .map(customerMapper::toDto).or(() -> {
                    throw new CustomerNotFoundException("Customer with id " + id + " not found");
                });
    }
    public void deleteCustomer(Long id) {

        customerRepositoryFacade.deleteById(id);
    }
    @Transactional
    public HttpResponse<?> updateCustomer(Long id, CustomerDTO customerDTO) {
        customerRepositoryFacade.findById(id)
                .map(existing -> {
                    Customer merged=  customerMapper.updateCustomer(existing, customerDTO);
                    return customerRepositoryFacade.update(merged);
                })
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + id + " not found"));

        return HttpResponse.ok("Updated!");
    }


}