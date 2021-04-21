package com.example.demo.model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "bank_account")
public class BankAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Bank name cannot be empty")
    private String bankName;
    @NotBlank(message = "Country cannot be empty")
    private String country;
    @NotBlank(message = "Account number cannot be empty")
    private String accountNumber;
    @NotBlank(message = "Owner name cannot be empty")
    private String ownerName;
    @NotBlank(message = "Address cannot be empty")
    private String address;
    @NotBlank(message = "Currency cannot be empty")
    private String currency;
    @Min(0)
    private int balance;
    @Min(0)
    private int bitcoins;

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


}
