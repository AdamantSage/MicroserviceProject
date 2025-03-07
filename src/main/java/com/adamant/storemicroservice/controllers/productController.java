package com.adamant.storemicroservice.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.adamant.storemicroservice.models.Product;
import com.adamant.storemicroservice.models.ProductDto;
import com.adamant.storemicroservice.services.productsRepository;

@Controller
@RequestMapping("/products")
public class productController {
    

    @Autowired
    private productsRepository repo;

    @GetMapping({"", "/"})
    public String showProductsList(Model model){
        List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "products/index";
    }


    @GetMapping("/create")
    public String showCreatePage(Model model){
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/createProduct";
    }
}
