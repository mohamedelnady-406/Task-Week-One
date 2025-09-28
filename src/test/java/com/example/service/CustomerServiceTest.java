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
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {
    CustomerRepositoryFacade repo = mock(CustomerRepositoryFacade.class);
    CustomerMapper mapper = mock(CustomerMapper.class);
    CustomerEventProducer kafka = mock(CustomerEventProducer.class);
    MessageProducer jms = mock(MessageProducer.class);

    CustomerService service =
            new CustomerService(repo, mapper, kafka, jms);

    @Test
    void addCustomer_sendsEventsAndSaves() {
        CustomerDTO dto = CustomerDTO.builder().name("Ali").email("a@b.com").build();
        Customer entity = new Customer();
        when(mapper.toEntity(dto)).thenReturn(entity);

        HttpResponse<?> resp = service.addCustomer(dto);

        verify(repo).save(entity);
        verify(kafka).sendCustomerCreated(anyString());
        verify(jms).send(anyString());
        assertEquals(200, resp.getStatus().getCode());
    }

    @Test
    void findById_notFound_throws() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class,
                () -> service.findById(99L));
    }

    @Test
    void getCustomers_returnsPagedDto() {
        Page<Customer> page = Page.empty();
        when(repo.findAll(Pageable.from(0))).thenReturn(page);
        when(mapper.toDto(any())).thenReturn(CustomerDTO.builder().build());

        Page<CustomerDTO> result = service.getCustomers(Pageable.from(0));

        assertTrue(result.isEmpty());
    }
    @Test
    void updateCustomer_updatesExisting() {
        CustomerDTO dto = CustomerDTO.builder().name("New").email("n@x.com").build();
        Customer existing = new Customer();
        Customer merged = new Customer();

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(mapper.updateCustomer(existing, dto)).thenReturn(merged);
        when(repo.update(merged)).thenReturn(merged);

        HttpResponse<?> resp = service.updateCustomer(1L, dto);

        verify(repo).update(merged);
        assertEquals(200, resp.getStatus().getCode());
    }

    @Test
    void updateCustomer_notFound_throws() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        CustomerDTO dto = CustomerDTO.builder().name("X").build();
        assertThrows(CustomerNotFoundException.class,
                () -> service.updateCustomer(1L, dto));
    }

    @Test
    void deleteCustomer_deletes() {
        service.deleteCustomer(5L);
        verify(repo).deleteById(5L);
    }
}
