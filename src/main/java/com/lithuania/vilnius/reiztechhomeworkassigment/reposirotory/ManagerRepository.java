package com.lithuania.vilnius.reiztechhomeworkassigment.reposirotory;

import com.lithuania.vilnius.reiztechhomeworkassigment.model.SupermarketManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends JpaRepository<SupermarketManager, Long> {

}
