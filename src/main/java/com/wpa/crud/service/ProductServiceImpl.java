package com.wpa.crud.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.wpa.crud.model.Product;
import com.wpa.crud.model.ProductDto;
import com.wpa.crud.repository.ProductsRepository;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductsRepository repo;

    @Override
    public List<Product> getAllProduct() {
        return repo.findAll();
    }

    @Override
    public void createProduct(ProductDto productDto) {
        Product product = new Product();
        setProductDetails(product, productDto);
        repo.save(product);
    }

    @Override
    public Product getProductById(int id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public void updateProduct(int id, ProductDto productDto) {
        Product product = getProductById(id);
        setProductDetails(product, productDto);
        repo.save(product);
    }

    @Override
    public void deleteProduct(int id) {
        Product product = getProductById(id);
        String imageFileName = product.getImageFileName();
        if (imageFileName != null && !imageFileName.isEmpty()) {
            deleteImageFile(imageFileName);
        }
        repo.deleteById(id);
    }

    private void setProductDetails(Product product, ProductDto productDto) {
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(new Date());

        MultipartFile image = productDto.getImageFile();
        if (image != null && !image.isEmpty()) {
            String originalFileName = image.getOriginalFilename();
            String storageFileName = generateUniqueFileName(originalFileName);
            try {
                String uploadDir = "public/images/"; 
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
                }
                product.setImageFileName(storageFileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store image file", e);
            }
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String fileExtension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            fileExtension = originalFileName.substring(i + 1);
        }
        return UUID.randomUUID().toString() + "." + fileExtension;
    }

    private void deleteImageFile(String imageFileName) {
        String uploadDir = "public/images/";
        Path filePath = Paths.get(uploadDir, imageFileName);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image file", e);
        }
    }
}
 