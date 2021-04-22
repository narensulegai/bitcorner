package com.example.demo.api;

import com.example.demo.model.CustomerEntity;
import com.example.demo.model.SellBitcoinEntity;
import com.example.demo.model.SendBillEntity;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.SellBitcoinRepository;
import com.example.demo.repository.SendBillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/sendBill")
public class SendBillController {

    @Autowired
    SendBillRepository sendBillRepository;

    @ResponseBody
    @GetMapping
    public List<SendBillEntity> get() {
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return sendBillRepository.findByCustomerId(customerEntity.getId());
    }

    @ResponseBody
    @PostMapping
    public SendBillEntity update(@RequestBody @Valid SendBillEntity sendBillEntity) {
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        sendBillEntity.setCustomer(customerEntity);
        return sendBillRepository.save(sendBillEntity);
    }
}
