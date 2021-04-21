package com.example.demo.model;

import com.sun.istack.Nullable;

import javax.persistence.*;

@Entity
@Table(name = "customer")
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(unique = true)
    String uid;

    @Column(unique = true)
    @Nullable
    String name;

    @Column(unique = true)
    @Nullable
    String email;

    @Nullable
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_account_id", referencedColumnName = "id")
    BankAccountEntity bankAccount;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BankAccountEntity getBankAccount() {
        return bankAccount;
    }

    public BankAccountEntity setBankAccount(BankAccountEntity bankAccount) {
        return this.bankAccount = bankAccount;
    }
}