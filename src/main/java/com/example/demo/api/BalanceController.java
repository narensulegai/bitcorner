package com.example.demo.api;

import com.example.demo.Currency;
import com.example.demo.model.BalanceEntity;
import com.example.demo.model.CustomerEntity;
import com.example.demo.model.TransactBitcoinEntity;
import com.example.demo.repository.BalanceRepository;
import com.example.demo.repository.TransactBitcoinRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/balance")
public class BalanceController {

    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    TransactBitcoinRepository transactBitcoinRepository;

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
    public JsonNode getExchangeRates() throws JsonProcessingException {
    	ObjectMapper mapper = new ObjectMapper();
    	Map<String, Map<String, Map<String, Double>>> rateMap = null;
    	try {
    	    rateMap = mapper.readValue(ClassLoader.getSystemClassLoader().getResourceAsStream("json/exchangeRates.json"), Map.class);

            Map<String, Map<String, Double>> current = rateMap.get("BITCOIN");
            Map<String, Double> currentRates = current.get("rates");

            for (Currency currency : Currency.values()) {
                TransactBitcoinEntity transactBitcoinEntity = transactBitcoinRepository.findFirstByCurrencyAndIsMarketOrderOrderByIdDesc(currency, false);
                if(transactBitcoinEntity != null) {
                    double rateAmount = (double) transactBitcoinEntity.getAmount();
                    currentRates.put(currency.toString(), rateAmount);
                    Map<String, Map<String, Double>> parentCurrency = rateMap.get(currency.toString());
                    Map<String, Double> parentRates = parentCurrency.get("rates");
                    parentRates.put("BITCOIN", 1/rateAmount);
                }
            }
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
        String json = new ObjectMapper().writeValueAsString(rateMap);
    	return new ObjectMapper().readTree(json);
    }
}
