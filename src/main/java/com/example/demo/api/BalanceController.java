package com.example.demo.api;

import com.example.demo.model.BalanceEntity;
import com.example.demo.model.CustomerEntity;
import com.example.demo.repository.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/balance")
public class BalanceController {

    @Autowired
    BalanceRepository balanceRepository;

    @ResponseBody
    @GetMapping
    public List<BalanceEntity> get() {
        var customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        var bankAccount = customerEntity.getBankAccount();
        return balanceRepository.findByBankAccountId(bankAccount.getId());
    }

    @ResponseBody
    @PostMapping
    public int create(@RequestBody @Valid List<BalanceEntity> balanceEntities) {
        var customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        var bankAccount = customerEntity.getBankAccount();
        for (var balanceEntity : balanceEntities) {
            balanceEntity.setBankAccount(bankAccount);
            balanceRepository.save(balanceEntity);
        }
        return balanceEntities.size();
    }

}
