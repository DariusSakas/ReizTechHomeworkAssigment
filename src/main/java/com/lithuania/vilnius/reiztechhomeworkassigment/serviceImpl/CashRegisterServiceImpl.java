package com.lithuania.vilnius.reiztechhomeworkassigment.serviceImpl;

import com.lithuania.vilnius.reiztechhomeworkassigment.exceptions.NotEnoughChangeException;
import com.lithuania.vilnius.reiztechhomeworkassigment.exceptions.PayNotAcceptedException;
import com.lithuania.vilnius.reiztechhomeworkassigment.exceptions.SupermarketServiceException;
import com.lithuania.vilnius.reiztechhomeworkassigment.model.Cash;
import com.lithuania.vilnius.reiztechhomeworkassigment.model.CashType;
import com.lithuania.vilnius.reiztechhomeworkassigment.reposirotory.CashRepository;
import com.lithuania.vilnius.reiztechhomeworkassigment.service.CashRegisterService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CashRegisterServiceImpl implements CashRegisterService {

    private static final String SUCCESSFUL_PAYMENT = "Thank you for your purchase! Please come again!";
    private static final String SUCCESSFUL_PAYMENT_WITH_CHANGE = "Thank you for your purchase! Here is your change  %,.2f. Please come again!";
    private static final String STILL_SOMETHING_LEFT_TO_PAY = "Cash accepted. Please pay %,.2f more to finnish your payment!";
    private static final String CASH_MACHINE_STUCK = "Cash machine got stuck. Please ask manager! Returning your %,.2f.";
    private static final String NOT_ENOUGH_CHANGE = "You're not the last one here. Your change: %,.2f. Bye.";
    private static final String INVALID_VALUE = "Invalid value of coin or bill. Please check and try again!";
    private static final String IMPORTANT_REDUNDANT = "https://i.pinimg.com/564x/8d/15/7f/8d157f1796c9a917e12fe86d1f0a88f4.jpg";

    public static final double COIN_OF_TEN = 0.10;
    public static final double COIN_OF_FIFTY = 0.50;
    public static final double BILL_OF_ONE = 1;
    public static final double BILL_OF_TWO = 2;

    private final CashRepository cashRepository;
    private final Basket basket;

    private Map<CashType, Long> coinsAndBillsPaidTempStorage;

    private double amountPaid;

    public CashRegisterServiceImpl(CashRepository cashRepository) {
        coinsAndBillsPaidTempStorage = new HashMap<>();
        basket = Basket.getInstance();
        this.cashRepository = cashRepository;
    }

    @Override
    public double getAmountPaid() {
        return amountPaid;
    }

    @Override
    public String tryToPayForSelectProduct(double amount) throws SupermarketServiceException {
        double productPrice = basket.getSelectedProduct().getPrice();

        if (!isAmountValid(amount))
            throw new PayNotAcceptedException(INVALID_VALUE);

        if (amount < 1) {
            payingWithCoin(amount);
            return checkIfFullyPaid(productPrice);
        }
        payingWithBill((int) amount);
        return checkIfFullyPaid(productPrice);
    }

    @Override
    public String customerChangedHisMindAndWantsToGoToAnotherShopAndTakesHisMoney() {
        double currentAmount = amountPaid;
        amountPaid = 0;
        coinsAndBillsPaidTempStorage = null;
        return String.format(NOT_ENOUGH_CHANGE, currentAmount);
    }

    private Boolean isAmountValid(double amount) {
        return amount == 0.1 || amount == 0.5 || amount == 1 || amount == 2;
    }

    private void payingWithCoin(double amount) {
        amountPaid += amount;
        switch ((int) (amount * 10)) {
            case 1:
                addPaidAmountToTempStorage(coinsAndBillsPaidTempStorage, CashType.COIN_OF_10);
                break;
            case 5:
                addPaidAmountToTempStorage(coinsAndBillsPaidTempStorage, CashType.COIN_OF_50);
                break;
        }
    }

    private void payingWithBill(int amount) {
        amountPaid += amount;
        switch (amount) {
            case 1:
                addPaidAmountToTempStorage(coinsAndBillsPaidTempStorage, CashType.BILL_OF_1);
                break;
            case 2:
                addPaidAmountToTempStorage(coinsAndBillsPaidTempStorage, CashType.BILL_OF_2);
                break;
        }
    }

    private void addPaidAmountToTempStorage(Map<CashType, Long> coinsAndBillsPaidTempStorage, CashType cashType) {
        if (!coinsAndBillsPaidTempStorage.containsKey(cashType))
            coinsAndBillsPaidTempStorage.put(cashType, 1L);
        else
            coinsAndBillsPaidTempStorage.put(cashType, coinsAndBillsPaidTempStorage.get(cashType) + 1);
    }

    private String checkIfFullyPaid(double productPrice) throws PayNotAcceptedException, NotEnoughChangeException {
        if (productPrice == amountPaid) {
            basket.setSelectedProduct(null);
            saveCoinsFromTempStorage(coinsAndBillsPaidTempStorage);
            return SUCCESSFUL_PAYMENT;
        } else if (productPrice > amountPaid) {
            double leftToPay = productPrice - amountPaid;
            return String.format(STILL_SOMETHING_LEFT_TO_PAY, leftToPay);
        }
        double changeAmount = amountPaid - productPrice;
        double change = getChangeFromDB(changeAmount);
        basket.setSelectedProduct(null);
        saveCoinsFromTempStorage(coinsAndBillsPaidTempStorage);
        return String.format(SUCCESSFUL_PAYMENT_WITH_CHANGE, change);
    }

    private void saveCoinsFromTempStorage(Map<CashType, Long> coinsAndBillsPaidTempStorage) throws PayNotAcceptedException {
        List<Cash> bagOfMoney = getListOfCoinsAndBillsFromDB();
        for (CashType cashType : coinsAndBillsPaidTempStorage.keySet()) {
            if (cashType == CashType.BILL_OF_2 && coinsAndBillsPaidTempStorage.get(CashType.BILL_OF_2) != null)
                addAmountFromTempStorageToBagOfMoney(bagOfMoney, coinsAndBillsPaidTempStorage, CashType.BILL_OF_2);

            else if (cashType == CashType.BILL_OF_1 && coinsAndBillsPaidTempStorage.get(CashType.BILL_OF_1) != null)
                addAmountFromTempStorageToBagOfMoney(bagOfMoney, coinsAndBillsPaidTempStorage, CashType.BILL_OF_1);

            else if (cashType == CashType.COIN_OF_50 && coinsAndBillsPaidTempStorage.get(CashType.COIN_OF_50) != null)
                addAmountFromTempStorageToBagOfMoney(bagOfMoney, coinsAndBillsPaidTempStorage, CashType.COIN_OF_50);

            else if (cashType == CashType.COIN_OF_10 && coinsAndBillsPaidTempStorage.get(CashType.COIN_OF_10) != null)
                addAmountFromTempStorageToBagOfMoney(bagOfMoney, coinsAndBillsPaidTempStorage, CashType.COIN_OF_10);
        }
        cashRepository.saveAll(bagOfMoney);
        coinsAndBillsPaidTempStorage.clear();
    }

    private void addAmountFromTempStorageToBagOfMoney(List<Cash> bagOfMoney, Map<CashType, Long> tempStorage, CashType cashType) {
        bagOfMoney.stream().filter(e -> e.getCashType() == cashType).findFirst().get().setAmount(
                tempStorage.get(cashType) + bagOfMoney.stream().filter(e -> e.getCashType() == cashType).findFirst().get().getAmount());
    }

    private double getChangeFromDB(double changeAmount) throws PayNotAcceptedException, NotEnoughChangeException {
        List<Cash> bagOfMoney = getListOfCoinsAndBillsFromDB();
        Map<CashType, Long> mapOfCoinsAndBillsWithTheirAmounts = bagOfMoney.stream().collect(Collectors.toMap(Cash::getCashType, Cash::getAmount));

        double changeToReturn = 0;
        //TODO: create general method to reduce code
        while (changeAmount > 0) {
            if (changeAmount >= BILL_OF_TWO && isThereAnyCoinOrBillInDB(mapOfCoinsAndBillsWithTheirAmounts.get(CashType.BILL_OF_2))) {
                changeAmount -= BILL_OF_TWO;
                changeToReturn += BILL_OF_TWO;
                calcChange(CashType.BILL_OF_2, mapOfCoinsAndBillsWithTheirAmounts, bagOfMoney);
            } else if (changeAmount >= BILL_OF_ONE && isThereAnyCoinOrBillInDB(mapOfCoinsAndBillsWithTheirAmounts.get(CashType.BILL_OF_1))) {
                changeAmount -= BILL_OF_ONE;
                changeToReturn += BILL_OF_ONE;
                calcChange(CashType.BILL_OF_1, mapOfCoinsAndBillsWithTheirAmounts, bagOfMoney);
            } else if (changeAmount >= COIN_OF_FIFTY && isThereAnyCoinOrBillInDB(mapOfCoinsAndBillsWithTheirAmounts.get(CashType.COIN_OF_50))) {
                changeAmount -= COIN_OF_FIFTY;
                changeToReturn += COIN_OF_FIFTY;
                calcChange(CashType.COIN_OF_50, mapOfCoinsAndBillsWithTheirAmounts, bagOfMoney);
            } else if (changeAmount >= COIN_OF_TEN && isThereAnyCoinOrBillInDB(mapOfCoinsAndBillsWithTheirAmounts.get(CashType.COIN_OF_10))) {
                changeAmount -= COIN_OF_TEN;
                changeToReturn += COIN_OF_TEN;
                calcChange(CashType.COIN_OF_10, mapOfCoinsAndBillsWithTheirAmounts, bagOfMoney);
            } else
                break;
        }

        cashRepository.saveAll(bagOfMoney);
        amountPaid = 0;
        return changeToReturn;
    }

    private void calcChange(CashType cashType, Map<CashType, Long> map, List<Cash> bagOfMoney) {
        map.put(cashType, map.get(cashType) - 1);
        bagOfMoney.stream().filter(e -> e.getCashType() == cashType).findFirst().get().setAmount(
                map.get(cashType));
    }

    private List<Cash> getListOfCoinsAndBillsFromDB() throws PayNotAcceptedException {
        List<Cash> bagOfMoney = cashRepository.findAll();
        if (bagOfMoney.isEmpty()) {
            throwCashMachineDoesntHaveEnoughChangePayNotAcceptedException();
        }
        return bagOfMoney;
    }

    private void throwCashMachineDoesntHaveEnoughChangePayNotAcceptedException() throws PayNotAcceptedException {
        double currentAmount = amountPaid;
        amountPaid = 0;
        coinsAndBillsPaidTempStorage.clear();
        throw new PayNotAcceptedException(String.format(CASH_MACHINE_STUCK, currentAmount));
    }

    private boolean isThereAnyCoinOrBillInDB(Long aLong) {
        return aLong > 0;
    }
}
