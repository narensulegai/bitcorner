package com.example.demo.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/bankAccount")
public class BankAccountController {

    @GetMapping(path = "{id}")
    @ResponseBody
    public String getBankAccountById(@PathVariable("id") Long id) {
        return id + "";
    }

}
