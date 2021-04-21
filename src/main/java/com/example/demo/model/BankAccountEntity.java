package com.example.demo.model;

import com.example.demo.Currency;

import javax.persistence.*;
import javax.validation.constraints.Min;
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
    Currency currency = Currency.USD;
    @Min(0)
    int balance = 0;
    @Min(0)
    int bitcoins = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getBitcoins() {
        return bitcoins;
    }

    public void setBitcoins(int bitcoins) {
        this.bitcoins = bitcoins;
    }

    public String getOwnerName() {return ownerName;}

    public void setOwnerName(String ownerName) {this.ownerName = ownerName;}

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

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

}
