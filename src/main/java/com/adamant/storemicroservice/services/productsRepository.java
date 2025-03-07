package com.adamant.storemicroservice.services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adamant.storemicroservice.models.Product;


//we use this interface to read and write products from the database
public interface productsRepository extends JpaRepository<Product, Integer>{
    
}
