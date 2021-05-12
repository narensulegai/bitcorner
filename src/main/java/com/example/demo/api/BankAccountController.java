package com.example.demo.api;

import com.example.demo.model.BalanceEntity;
import com.example.demo.model.BankAccountEntity;
import com.example.demo.model.CustomerEntity;
import com.example.demo.Currency;
import com.example.demo.repository.BalanceRepository;
import com.example.demo.repository.BankAccountRepository;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/bankAccount")
public class BankAccountController {

    @Autowired
    CustomerRepository customerRepository;
    
    @Autowired
    BalanceRepository balanceRepository; 
    
    @Autowired
    BankAccountRepository bankAccountRepository; 

    @ResponseBody
    @GetMapping
    public Optional<BankAccountEntity> getBankAccount() {
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return Optional.ofNullable(customerEntity.getBankAccount());
    }

    @ResponseBody
    @PostMapping
    public BankAccountEntity updateBankAccount(@RequestBody @Valid BankAccountEntity bankAccountEntity) {
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        
        if(customerEntity.getBankAccount() != null)
        	bankAccountEntity.setId(customerEntity.getBankAccount().getId());

        customerEntity.setBankAccount(bankAccountEntity);
        customerRepository.save(customerEntity);  
        
        CustomerEntity sameCustomer = customerRepository.findById(customerEntity.getId()).get();
                
        if(balanceRepository.findByBankAccountId(bankAccountEntity.getId()).size() == 0) {
        	for(Currency c: Currency.values()) {
                BalanceEntity balanceEntity = new BalanceEntity();
                balanceEntity.setBankAccount(sameCustomer.getBankAccount());
                balanceEntity.setBalance(0);
                balanceEntity.setCurrency(c);
                balanceRepository.save(balanceEntity);
            }
        }
        
        return customerEntity.getBankAccount();
    }
}
