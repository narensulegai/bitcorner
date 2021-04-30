package com.example.demo.api;

import com.example.demo.model.CustomerEntity;
import com.example.demo.model.SellBitcoinEntity;
import com.example.demo.model.TransactBitcoinEntity;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.SellBitcoinRepository;
import com.example.demo.repository.TransactBitcoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/transactBitcoin")
public class TransactBitcoinController {

    @Autowired
    TransactBitcoinRepository transactBitcoinRepository;
    @Autowired
    CustomerRepository customerRepository;

    @ResponseBody
    @GetMapping
    public List<TransactBitcoinEntity> get() {
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return transactBitcoinRepository.findByCustomerId(customerEntity.getId());
    }

    @ResponseBody
    @PostMapping
    public TransactBitcoinEntity update(@RequestBody @Valid TransactBitcoinEntity sellBitcoinEntity) {
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        sellBitcoinEntity.setCustomer(customerEntity);
        return transactBitcoinRepository.save(sellBitcoinEntity);
    }
}
