package com.example.demo.api;

import com.example.demo.model.TransactBitcoinEntity;
import com.example.demo.repository.TransactBitcoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/prevailingRates")
public class PrevailingRatesController {

    @Autowired
    TransactBitcoinRepository transactBitcoinRepository;

    @ResponseBody
    @GetMapping
    public List<TransactBitcoinEntity> get() {
        return transactBitcoinRepository.findAll();
    }


}
