package com.wpa.crud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.wpa.crud.model.Product;
import com.wpa.crud.model.ProductDto;
import com.wpa.crud.service.ProductService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductService productService;

    @GetMapping({"", "/"})
    public String showProductList(Model model) {
        List<Product> products = productService.getAllProduct();
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        model.addAttribute("productDto", new ProductDto());
        return "products/CreateProduct";
    }

    @PostMapping("/create")
    public String createProduct(@ModelAttribute("productDto") @Valid ProductDto productDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "products/CreateProduct";
        }

        MultipartFile imageFile = productDto.getImageFile();
        if (imageFile.isEmpty()) {
            result.rejectValue("imageFile", "error.imageFile", "Please select an image file to upload.");
            return "products/CreateProduct";
        }

        try {
            productService.createProduct(productDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error while creating product: " + e.getMessage());
            return "products/CreateProduct";
        }

        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditPage(@PathVariable int id, Model model) {
        Product product = productService.getProductById(id);
        ProductDto productDto = new ProductDto();
        productDto.setName(product.getName());
        productDto.setBrand(product.getBrand());
        productDto.setCategory(product.getCategory());
        productDto.setPrice(product.getPrice());
        productDto.setDescription(product.getDescription());
        model.addAttribute("productDto", productDto);
        model.addAttribute("productId", id);
        return "products/EditProduct";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable int id,@Valid @ModelAttribute("productDto") ProductDto productDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "products/EditProduct";
        }

        try {
            productService.updateProduct(id, productDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error while updating product: " + e.getMessage());
            return "products/EditProduct";
        }

        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}
