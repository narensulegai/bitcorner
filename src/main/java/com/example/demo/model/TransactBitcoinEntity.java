package com.example.demo.model;

import com.example.demo.Currency;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Table(name = "transact_bitcoin")
public class TransactBitcoinEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne
    CustomerEntity customer = null;

    @Min(0)
    int amount = 0;

    @Enumerated(EnumType.STRING)
    Currency currency = Currency.USD;

    boolean isMarketOrder = true;

    boolean isBuy = true;

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean isBuy) {
        this.isBuy = isBuy;
    }

    Integer minPrice = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public boolean isMarketOrder() {
        return isMarketOrder;
    }

    public void setMarketOrder(boolean marketOrder) {
        isMarketOrder = marketOrder;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }
}
