package com.example.demo.model;

import javax.persistence.*;
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
    @NotBlank(message = "Primary currency cannot be empty")
    private String currency;

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
