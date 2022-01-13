package com.lithuania.vilnius.reiztechhomeworkassigment.controller;

import com.lithuania.vilnius.reiztechhomeworkassigment.exceptions.SupermarketServiceException;
import com.lithuania.vilnius.reiztechhomeworkassigment.model.Product;
import com.lithuania.vilnius.reiztechhomeworkassigment.service.SupermarketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SupermarketController {

    private final SupermarketService supermarketService;

    public SupermarketController(SupermarketService supermarketService) {
        this.supermarketService = supermarketService;
    }

    @GetMapping("/getProducts")
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(supermarketService.getAllProducts(), HttpStatus.OK);
    }

    @PutMapping("/selectProduct/{id}")
    public ResponseEntity<Product> selectProductToBuy(@PathVariable(name = "id") Long id) throws SupermarketServiceException {
        return new ResponseEntity<>(supermarketService.selectProductToBuy(id), HttpStatus.OK);
    }

    @PutMapping("/pay")
    public ResponseEntity<String> payForProduct(@RequestParam double amount) throws SupermarketServiceException {
        return new ResponseEntity<>(supermarketService.payForProduct(amount), HttpStatus.OK);
    }

    @PostMapping("/returnProduct")
    public ResponseEntity<String> returnProduct() throws SupermarketServiceException {
        return new ResponseEntity<>( supermarketService.returnProduct(), HttpStatus.OK);
    }

    @ExceptionHandler(SupermarketServiceException.class)
    public ResponseEntity<String> catchSoldOutException(SupermarketServiceException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.I_AM_A_TEAPOT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> catchExceptions(RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
