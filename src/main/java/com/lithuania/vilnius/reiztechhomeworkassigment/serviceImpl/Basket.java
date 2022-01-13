package com.lithuania.vilnius.reiztechhomeworkassigment.serviceImpl;

import com.lithuania.vilnius.reiztechhomeworkassigment.exceptions.CanNotSelectTheProduct;
import com.lithuania.vilnius.reiztechhomeworkassigment.exceptions.SoldOutException;
import com.lithuania.vilnius.reiztechhomeworkassigment.exceptions.SupermarketServiceException;
import com.lithuania.vilnius.reiztechhomeworkassigment.model.Product;
import com.lithuania.vilnius.reiztechhomeworkassigment.reposirotory.ProductsRepository;

public class Basket {
    private static Basket basket;

    public Basket() {
    }

    public static Basket getInstance() {
        if (basket == null)
            basket = new Basket();
        return basket;
    }

    private Product selectedProduct;

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public Boolean isAnyProductInBasket(){
        return this.selectedProduct != null;
    }

    public void addProductToBasket(Product product, ProductsRepository productsRepository) throws SupermarketServiceException {
        if (basket.isAnyProductInBasket())
            throw new CanNotSelectTheProduct("You already have one product in your basket. " +
                    "Please continue your purchase or return product back to shelf. Thank you!");

        Long quantity = product.getQuantity() - SupermarketServiceImpl.SELECTED_AMOUNT;
        Product productWithoutOneInBasket = new Product(
                product.getId(), product.getName(), product.getPrice(), quantity);
        productsRepository.save(productWithoutOneInBasket);

        basket.setSelectedProduct(product);
    }

}
