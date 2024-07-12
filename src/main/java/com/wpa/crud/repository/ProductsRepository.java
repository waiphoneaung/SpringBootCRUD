package com.wpa.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wpa.crud.model.Product;

public interface ProductsRepository extends JpaRepository<Product, Integer>{
	
}
