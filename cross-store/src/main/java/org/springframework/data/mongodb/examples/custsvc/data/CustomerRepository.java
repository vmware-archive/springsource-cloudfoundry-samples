package org.springframework.data.mongodb.examples.custsvc.data;

import java.util.List;

import org.springframework.data.mongodb.examples.custsvc.domain.Customer;

public interface CustomerRepository {

    List<Customer> findAll();

    Customer findOne(Long id);

    void save(Customer customer);

    void delete(Customer customer);
}
