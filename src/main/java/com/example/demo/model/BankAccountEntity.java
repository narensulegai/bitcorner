package com.example.demo.model;

import com.example.demo.Currency;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "bank_account")
public class BankAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @NotBlank(message = "Bank name cannot be empty")
    String bankName;
    @NotBlank(message = "Country cannot be empty")
    String country;
    @NotBlank(message = "Account number cannot be empty")
    String accountNumber;
    @NotBlank(message = "Owner name cannot be empty")
    String ownerName;
    @NotBlank(message = "Address cannot be empty")
    String address;
    @Enumerated(EnumType.STRING)
    Currency primaryCurrency = Currency.USD;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Currency getPrimaryCurrency() {
        return primaryCurrency;
    }

    public void setPrimaryCurrency(Currency primaryCurrency) {
        this.primaryCurrency = primaryCurrency;
    }

}
