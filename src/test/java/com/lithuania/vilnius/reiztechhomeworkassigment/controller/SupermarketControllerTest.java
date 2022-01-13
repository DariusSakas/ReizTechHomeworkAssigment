package com.lithuania.vilnius.reiztechhomeworkassigment.controller;

import com.google.gson.Gson;
import com.lithuania.vilnius.reiztechhomeworkassigment.exceptions.SoldOutException;
import com.lithuania.vilnius.reiztechhomeworkassigment.model.Product;
import com.lithuania.vilnius.reiztechhomeworkassigment.service.SupermarketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.lithuania.vilnius.reiztechhomeworkassigment.serviceImpl.SupermarketServiceImpl.CANT_FIND_SUCH_PRODUCT;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = SupermarketController.class)
public class SupermarketControllerTest {

    private static final String GET_ALL_PRODUCTS_REQ = "/getProducts";
    private static final String FIND_BY_ID_REQ = "/selectProduct/{id}";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupermarketService supermarketServiceMock;

    @Test
    void getAllProductsWhenFoundAll() throws Exception {

        List<Product> productList = new ArrayList<>(Arrays.asList(
                new Product("Test1", 1.0), new Product("Test2", 2.0))
        );
        given(supermarketServiceMock.getAllProducts()).willReturn(productList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(GET_ALL_PRODUCTS_REQ);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(new Gson().toJson(productList)));
    }

    @Test
    void getAllProductsWhenFoundNone() throws Exception {

        given(supermarketServiceMock.getAllProducts()).willReturn(new ArrayList<>());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(GET_ALL_PRODUCTS_REQ);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"));
    }

    @Test
    void selectProductToBuyByIDOne() throws Exception {
        Product product = new Product(1L, "Test", 1.0, 1L);
        given(supermarketServiceMock.selectProductToBuy(1L)).willReturn(product);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(FIND_BY_ID_REQ, 1);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(new Gson().toJson(product)));
    }

    @Test
    void selectProductToBuyThrowsSoldOutException() throws Exception {

        given(supermarketServiceMock.selectProductToBuy(1L)).willThrow(new SoldOutException(CANT_FIND_SUCH_PRODUCT));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(FIND_BY_ID_REQ, 1);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(content().string(CANT_FIND_SUCH_PRODUCT));
    }
}
