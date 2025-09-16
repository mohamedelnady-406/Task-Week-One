package com.example.mapper;

import com.example.dtos.CustomerDTO;
import com.example.entity.Customer;
import io.micronaut.context.annotation.Mapper;

import jakarta.inject.Singleton;

import java.lang.annotation.Target;


@Singleton
public interface CustomerMapper {

    @Mapper
    CustomerDTO toDto(Customer customer);

    @Mapper
    Customer toEntity(CustomerDTO dto);
    @Mapper
    Customer updateCustomer(Customer target, CustomerDTO source);

}
