package com.example.demo.model;

import com.example.demo.Currency;
import com.example.demo.OrderStatus;

import java.math.BigDecimal;

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

    @Column(nullable = true)
    @Min(0)
    BigDecimal amount = BigDecimal.ZERO;

    @Min(0)
    int bitcoins = 0;

    @Enumerated(EnumType.STRING)
    Currency currency = Currency.USD;

    @Enumerated(EnumType.STRING)
    OrderStatus status = OrderStatus.OPEN;

    public int getBitcoins() {
        return bitcoins;
    }

    public void setBitcoins(int bitcoins) {
        this.bitcoins = bitcoins;
    }

    boolean isMarketOrder = true;

    boolean isBuy = true;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean isBuy) {
        this.isBuy = isBuy;
    }

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
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
}
