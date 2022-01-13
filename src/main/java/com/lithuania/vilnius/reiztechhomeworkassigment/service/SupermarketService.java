package com.lithuania.vilnius.reiztechhomeworkassigment.service;

import com.lithuania.vilnius.reiztechhomeworkassigment.exceptions.SoldOutException;
import com.lithuania.vilnius.reiztechhomeworkassigment.exceptions.SupermarketServiceException;
import com.lithuania.vilnius.reiztechhomeworkassigment.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface SupermarketService {

    List<Product> getAllProducts();

    Product selectProductToBuy(Long id) throws SupermarketServiceException;

    String payForProduct(double amount) throws SupermarketServiceException;

    String returnProduct() throws SupermarketServiceException;

}
