package com.lithuania.vilnius.reiztechhomeworkassigment.model;

import javax.persistence.*;

@Entity
public class Cash {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private CashType cashType;
    private Long amount;

    public Cash(CashType cashType, Long amount) {
        this.cashType = cashType;
        this.amount = amount;
    }

    public Cash() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CashType getCashType() {
        return cashType;
    }

    public void setCashType(CashType cashType) {
        this.cashType = cashType;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
