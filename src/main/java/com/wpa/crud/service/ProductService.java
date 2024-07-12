 package com.wpa.crud.service;

import java.util.List;
import com.wpa.crud.model.Product;
import com.wpa.crud.model.ProductDto;

public interface ProductService {
    List<Product> getAllProduct();
    void createProduct(ProductDto productDto);
    Product getProductById(int id);
    void updateProduct(int id, ProductDto productDto);
    void deleteProduct(int id);
}
