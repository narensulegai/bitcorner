package com.example.demo.api;

import com.example.demo.model.BalanceEntity;
import com.example.demo.model.CustomerEntity;
import com.example.demo.repository.BalanceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    
    @ResponseBody
    @GetMapping(path = "/rates")
    public Map<String, Object> getExchangeRates()  {
    	ObjectMapper mapper = new ObjectMapper();
    	Map<String, Object> rateMap = null;
    	try {
    	   rateMap = mapper.readValue(ClassLoader.getSystemClassLoader().getResourceAsStream("json/exchangeRates.json"), Map.class);
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
        return rateMap;
    }
}
