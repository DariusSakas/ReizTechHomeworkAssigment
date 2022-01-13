package com.lithuania.vilnius.reiztechhomeworkassigment.config;

import com.lithuania.vilnius.reiztechhomeworkassigment.model.Cash;
import com.lithuania.vilnius.reiztechhomeworkassigment.model.CashType;
import com.lithuania.vilnius.reiztechhomeworkassigment.model.Product;
import com.lithuania.vilnius.reiztechhomeworkassigment.model.SupermarketManager;
import com.lithuania.vilnius.reiztechhomeworkassigment.reposirotory.CashRepository;
import com.lithuania.vilnius.reiztechhomeworkassigment.reposirotory.ManagerRepository;
import com.lithuania.vilnius.reiztechhomeworkassigment.reposirotory.ProductsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class EntitiesLoader implements CommandLineRunner {

    private final ProductsRepository productsRepository;
    private final ManagerRepository managerRepository;
    private final CashRepository cashRegisterRepository;

    public EntitiesLoader(ProductsRepository productsRepository, ManagerRepository managerRepository, CashRepository cashRegisterRepository) {
        this.productsRepository = productsRepository;
        this.managerRepository = managerRepository;
        this.cashRegisterRepository = cashRegisterRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        //TODO: add password encoder
        SupermarketManager supermarketManager = new SupermarketManager("admin", "admin");
        managerRepository.save(supermarketManager);

        Product product1 = new Product("COLA", 1.5, 3L);
        Product product2 = new Product("SODA", 2.4, 4L);
        Product product3 = new Product("PEPSI", 1.8,2L);
        Product product4 = new Product("SPRITE", 2.0, 1L);
        Product product5 = new Product("GIRA", 1.90, 10L);
        Product product6 = new Product("Vandenelis", 0.90, 0L);
        productsRepository.saveAll(Arrays.asList(product1, product2, product3, product4,product5, product6));

        Cash cashRegister1 = new Cash(CashType.COIN_OF_10, 100L);
        Cash cashRegister2 = new Cash(CashType.COIN_OF_50, 100L);
        Cash cashRegister3 = new Cash(CashType.BILL_OF_1, 100L);
        Cash cashRegister4 = new Cash(CashType.BILL_OF_2, 100L);
        cashRegisterRepository.saveAll(Arrays.asList(cashRegister1, cashRegister2, cashRegister3, cashRegister4));

    }
}
