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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/balance")
public class BalanceController {

    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    TransactBitcoinRepository transactBitcoinRepository;
    //    Escaped using
    //    https://codebeautify.org/json-escape-unescape
    String exchangeRate = "{\"USD\" : {\"rates\" : {     \"INR\": 75.014195,     \"RMB\": 0.164322,     \"EUR\": 0.806942,     \"GBP\": 0.719154, \"BITCOIN\": 0.0000179, \"USD\": 1     }}, \"EUR\" : {\"rates\" : {     \"INR\": 89.163887,     \"RMB\": 7.824776,     \"USD\": 1.216459,     \"GBP\": 0.870096, \"BITCOIN\": 0.0000217, \"EUR\": 1}},\"INR\" : {\"rates\" : {     \"EUR\": 0.011215,     \"RMB\": 0.087760,     \"USD\": 0.013643,     \"GBP\": 0.009762, \"BITCOIN\": 0.000000244, \"INR\": 1 }},\"GBP\" : {\"rates\" : {     \"INR\": 102.430618,     \"RMB\": 8.989342,     \"USD\": 1.397490,     \"EUR\": 1.148825, \"BITCOIN\": 0.0000253, \"GBP\": 1 }},\"RMB\" : {\"rates\" : {     \"INR\": 11.394643,     \"EUR\": 0.127799,     \"USD\": 0.155461,     \"GBP\": 0.111246, \"BITCOIN\": 0.00000279, \"RMB\": 1 }},\"BITCOIN\" : {\"rates\" : {\"USD\": 56000.34,\"INR\": 4103026.50,\"EUR\": 46149.26,\"RMB\": 358190.20,\"GBP\": 39513.69,\"BITCOIN\": 1}},\"ServiceRate\" : 0.0001}";

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
    	Map<String, Map<String, Map<String, BigDecimal>>> rateMap = null;
    	try {
//            rateMap = mapper.readValue(ClassLoader.getSystemClassLoader().getResourceAsStream("src/main/resources/json/exchangeRates.json"), Map.class);
    	    rateMap = mapper.readValue(exchangeRate, Map.class);

            Map<String, Map<String, BigDecimal>> current = rateMap.get("BITCOIN");
            Map<String, BigDecimal> currentRates = current.get("rates");

            for (Currency currency : Currency.values()) {
                TransactBitcoinEntity transactBitcoinEntity = transactBitcoinRepository.findFirstByCurrencyAndIsMarketOrderOrderByIdDesc(currency, false);
                if(transactBitcoinEntity != null) {
                    BigDecimal rateAmount = transactBitcoinEntity.getAmount();
                    currentRates.put(currency.toString(), rateAmount);
                    Map<String, Map<String, BigDecimal>> parentCurrency = rateMap.get(currency.toString());
                    Map<String, BigDecimal> parentRates = parentCurrency.get("rates");
                    // https://stackoverflow.com/questions/4591206/arithmeticexception-non-terminating-decimal-expansion-no-exact-representable
                    parentRates.put("BITCOIN", BigDecimal.valueOf(1).divide(rateAmount, 2, RoundingMode.HALF_UP));
                }
            }
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
        String json = new ObjectMapper().writeValueAsString(rateMap);
    	return new ObjectMapper().readTree(json);
    }
}
