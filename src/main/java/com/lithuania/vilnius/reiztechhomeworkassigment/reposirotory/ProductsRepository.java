package com.lithuania.vilnius.reiztechhomeworkassigment.reposirotory;

import com.lithuania.vilnius.reiztechhomeworkassigment.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Long> {

}
