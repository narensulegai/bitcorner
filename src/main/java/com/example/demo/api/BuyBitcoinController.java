package com.example.demo.api;

import com.example.demo.model.BuyBitcoinEntity;
import com.example.demo.model.CustomerEntity;
import com.example.demo.model.SellBitcoinEntity;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.BuyBitcoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/buyBitcoin")
public class BuyBitcoinController {

    @Autowired
    BuyBitcoinRepository buyBitcoinRepository;
    @Autowired
    CustomerRepository customerRepository;

    @ResponseBody
    @GetMapping
    public List<BuyBitcoinEntity> get() {
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return buyBitcoinRepository.findByCustomerId(customerEntity.getId());
    }

    @ResponseBody
    @PostMapping
    public BuyBitcoinEntity update(@RequestBody @Valid BuyBitcoinEntity buyBitcoinEntity) {
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        buyBitcoinEntity.setCustomer(customerEntity);
        return buyBitcoinRepository.save(buyBitcoinEntity);
    }
}
