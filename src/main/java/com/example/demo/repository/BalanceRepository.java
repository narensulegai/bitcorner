package com.example.demo.repository;

import com.example.demo.Currency;
import com.example.demo.model.BalanceEntity;
import com.example.demo.model.BankAccountEntity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BalanceRepository extends CrudRepository<BalanceEntity, Long> {
    public List<BalanceEntity> findByBankAccountId(Long id);
    public BalanceEntity findByBankAccountAndCurrency(BankAccountEntity account, Currency currency);

}
