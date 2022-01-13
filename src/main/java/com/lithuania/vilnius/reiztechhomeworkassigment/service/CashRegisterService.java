package com.lithuania.vilnius.reiztechhomeworkassigment.service;

import com.lithuania.vilnius.reiztechhomeworkassigment.exceptions.SupermarketServiceException;
import com.lithuania.vilnius.reiztechhomeworkassigment.model.Product;
import org.springframework.stereotype.Service;


public interface CashRegisterService {

    String tryToPayForSelectProduct(double amount) throws SupermarketServiceException;

    String customerChangedHisMindAndWantsToGoToAnotherShopAndTakesHisMoney();

    double getAmountPaid();
}
