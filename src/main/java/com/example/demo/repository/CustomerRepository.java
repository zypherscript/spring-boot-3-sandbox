package com.example.demo.repository;

import com.example.demo.entity.Customer;
import java.util.Collection;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

  Collection<Customer> findByName(String name);
}
