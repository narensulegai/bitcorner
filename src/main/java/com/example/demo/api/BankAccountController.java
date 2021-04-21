package com.example.demo.api;

import com.example.demo.model.BankAccountEntity;
import com.example.demo.model.CustomerEntity;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(path = "/bankAccount")
public class BankAccountController {

    @Autowired
    CustomerRepository customerRepository;

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

        customerEntity.setBankAccount(bankAccountEntity);
        customerRepository.save(customerEntity);
        return customerEntity.getBankAccount();
    }
}
