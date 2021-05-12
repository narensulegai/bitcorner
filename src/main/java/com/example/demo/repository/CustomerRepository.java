package com.example.demo.repository;

import com.example.demo.model.CustomerEntity;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<CustomerEntity, Long> {
    CustomerEntity findByUid(String uid);
    CustomerEntity findByName(String name);
	CustomerEntity findByEmail(String email);
}
