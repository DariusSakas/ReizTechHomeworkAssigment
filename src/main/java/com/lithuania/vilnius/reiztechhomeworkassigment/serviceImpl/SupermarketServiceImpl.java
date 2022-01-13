package com.lithuania.vilnius.reiztechhomeworkassigment.serviceImpl;

import com.lithuania.vilnius.reiztechhomeworkassigment.exceptions.CanNotSelectTheProduct;
import com.lithuania.vilnius.reiztechhomeworkassigment.exceptions.PayNotAcceptedException;
import com.lithuania.vilnius.reiztechhomeworkassigment.exceptions.SoldOutException;
import com.lithuania.vilnius.reiztechhomeworkassigment.exceptions.SupermarketServiceException;
import com.lithuania.vilnius.reiztechhomeworkassigment.model.Product;
import com.lithuania.vilnius.reiztechhomeworkassigment.reposirotory.ProductsRepository;
import com.lithuania.vilnius.reiztechhomeworkassigment.service.CashRegisterService;
import com.lithuania.vilnius.reiztechhomeworkassigment.service.SupermarketService;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SupermarketServiceImpl implements SupermarketService {

    public static final Long SELECTED_AMOUNT = 1L;
    public static final String OUT_OF_STOCK = "Out of stock! Please ask manager or choose another product";
    public static final String CANT_FIND_SUCH_PRODUCT = "Can't find such product. Please try again";
    public static final String NO_PRODUCT_SELECTED = "No product selected. Please select product first!";
    public static final String BASKET_EMPTY = "You don't have any product in your basket!";
    public static final String HAVE_A_NICE_DAY = "%s returned. We hope to see you again! Have a nice day!";

    private final ProductsRepository productsRepository;
    private final CashRegisterService cashRegisterService;

    private final Basket basket = Basket.getInstance();

    public SupermarketServiceImpl(ProductsRepository productsRepository, CashRegisterService cashRegisterService) {
        this.productsRepository = productsRepository;
        this.cashRegisterService = cashRegisterService;
    }

    @Override
    public List<Product> getAllProducts() {
        Optional<List<Product>> optionalProducts = Optional.of(productsRepository.findAll());
        return optionalProducts.orElse(new ArrayList<>());
    }

    @Override
    public Product selectProductToBuy(Long id) throws SupermarketServiceException {
        Optional<Product> selectedProduct = productsRepository.findById(id);
        if (selectedProduct.isEmpty()) {
            throw new SoldOutException(CANT_FIND_SUCH_PRODUCT);
        }
        keepWorkingWithSelectedProduct(selectedProduct.get(), productsRepository);
        return new Product(selectedProduct.get().getId(), selectedProduct.get().getName(), selectedProduct.get().getPrice(), SELECTED_AMOUNT);
    }

    @Override
    public String payForProduct(double amount) throws SupermarketServiceException {
        if (!basket.isAnyProductInBasket())
            throw new PayNotAcceptedException(NO_PRODUCT_SELECTED);
        return cashRegisterService.tryToPayForSelectProduct(amount);
    }

    @Override
    public String returnProduct() throws SupermarketServiceException {
        if (!basket.isAnyProductInBasket())
            throw new CanNotSelectTheProduct(BASKET_EMPTY);
        return returnProductBackToSupermarketAndGoHome();
    }

    private String returnProductBackToSupermarketAndGoHome() {
        Product productToReturn = basket.getSelectedProduct();
        Product productFromDb = productsRepository.getById(productToReturn.getId());
        productFromDb.setQuantity(productFromDb.getQuantity() + 1);
        productsRepository.save(productFromDb);
        basket.setSelectedProduct(null);

        if (cashRegisterService.getAmountPaid() >= 0.1)
            return cashRegisterService.customerChangedHisMindAndWantsToGoToAnotherShopAndTakesHisMoney();
        return String.format(HAVE_A_NICE_DAY, productToReturn.getName());
    }

    private void keepWorkingWithSelectedProduct(Product product, ProductsRepository productsRepo) throws SupermarketServiceException {
        if (isProductAmountZero(product)) {
            throw new SoldOutException(OUT_OF_STOCK);
        }
        basket.addProductToBasket(product, productsRepo);
    }

    private Boolean isProductAmountZero(Product product) {
        return product.getQuantity() < 1;
    }

}
