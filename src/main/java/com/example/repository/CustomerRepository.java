package com.example.repository;

import com.example.entity.Customer;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer,Long> {
    Page<Customer> findAll(Pageable pageable);

}
