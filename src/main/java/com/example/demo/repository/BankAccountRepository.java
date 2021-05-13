package com.example.demo.repository;

import com.example.demo.model.BankAccountEntity;
import org.springframework.data.repository.CrudRepository;


public interface BankAccountRepository extends CrudRepository<BankAccountEntity, Long> {
	
	BankAccountEntity findByAccountNumber(String account);
   
}
