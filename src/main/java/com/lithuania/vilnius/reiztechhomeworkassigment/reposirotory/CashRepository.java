package com.lithuania.vilnius.reiztechhomeworkassigment.reposirotory;

import com.lithuania.vilnius.reiztechhomeworkassigment.model.Cash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashRepository extends JpaRepository<Cash, Long> {

}
