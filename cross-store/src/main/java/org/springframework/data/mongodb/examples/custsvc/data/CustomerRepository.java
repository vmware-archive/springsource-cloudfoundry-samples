package org.springframework.data.mongodb.examples.custsvc.data;

import org.springframework.data.mongodb.examples.custsvc.domain.Customer;

import java.util.List;

public interface CustomerRepository {

    List<Customer> findAll();

    Customer findOne(Long id);

    void save(Customer customer);

    void delete(Customer customer);
}
