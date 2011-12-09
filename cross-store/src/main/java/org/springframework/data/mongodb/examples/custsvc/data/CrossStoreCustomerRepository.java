package org.springframework.data.mongodb.examples.custsvc.data;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.mongodb.examples.custsvc.domain.Customer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class CrossStoreCustomerRepository implements CustomerRepository {

    @PersistenceContext
    EntityManager em;

    @Override
    public List<Customer> findAll() {
        return em.createQuery("select c from Customer c", Customer.class).getResultList();
    }

    @Override
    public Customer findOne(Long id) {
        return em.find(Customer.class, id);
    }

    @Override
    public void save(Customer customer) {
        em.merge(customer);
    }

    @Override
    public void delete(Customer customer) {
        em.remove(customer);
    }

}
