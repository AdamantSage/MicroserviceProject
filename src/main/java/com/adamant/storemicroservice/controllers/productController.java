package com.adamant.storemicroservice.controllers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adamant.storemicroservice.models.Product;
import com.adamant.storemicroservice.models.ProductDto;
import com.adamant.storemicroservice.services.productsRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/products")
public class productController {

    @Autowired
    private productsRepository repo;

    @GetMapping({ "", "/" })
    public String showProductsList(Model model) {
        List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "products/index";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String showCreatePage(Model model) {
        model.addAttribute("productDto", new ProductDto());
        return "products/createProduct";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute ProductDto productDto, BindingResult result) {

        if (productDto.getImageFile().isEmpty()) {
            result.addError((new FieldError("productDto", "imageFile", "The image file is required")));
        }

        // checking for validation error

        if (result.hasErrors()) {
            return "products/createProduct";
        }

        // save image file
        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        // saving to database

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImageFileName(storageFileName);

        repo.save(product);

        return "redirect:/products";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditPage(
            Model model, @PathVariable int id) { // Use @PathVariable here

        try {
            Product product = repo.findById(id).get();

            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);
            model.addAttribute("product", product);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/products";
        }
        return "products/editProducts";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable int id,
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result,
            Model model) {

        try {
            Product existingProduct = repo.findById(id).get();

            // If there are validation errors, return to the edit page
            if (result.hasErrors()) {
                model.addAttribute("product", existingProduct);
                return "products/editProducts";
            }

            // Update product fields
            existingProduct.setName(productDto.getName());
            existingProduct.setBrand(productDto.getBrand());
            existingProduct.setCategory(productDto.getCategory());
            existingProduct.setPrice(productDto.getPrice());
            existingProduct.setDescription(productDto.getDescription());

            // Handle image upload if a new image is provided
            if (!productDto.getImageFile().isEmpty()) {
                MultipartFile image = productDto.getImageFile();
                String storageFileName = new Date().getTime() + "_" + image.getOriginalFilename();

                try {
                    String uploadDir = "public/images/";
                    Path uploadPath = Paths.get(uploadDir);

                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    try (InputStream inputStream = image.getInputStream()) {
                        Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                                StandardCopyOption.REPLACE_EXISTING);
                    }

                    // Update image file name
                    existingProduct.setImageFileName(storageFileName);

                } catch (Exception ex) {
                    System.out.println("Exception: " + ex.getMessage());
                }
            }

            // Save updated product
            repo.save(existingProduct);

            return "redirect:/products";
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/products";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id) {
        try {
            Product product = repo.findById(id).get();

            // First delete the image file
            Path imagePath = Paths.get("public/images/" + product.getImageFileName());
            try {
                Files.delete(imagePath);
            } catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
            }

            // Then delete the product from database
            repo.delete(product);

            return "redirect:/products";
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/serve-image/{id}")
    @ResponseBody
    public byte[] serveProductImage(@PathVariable int id) {
        Product product = repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        String fileName = product.getImageFileName();
        try {
            Path path = Paths.get("public/images/" + fileName);
            return Files.readAllBytes(path);
        } catch (Exception ex) {
            throw new RuntimeException("Could not read image file: " + ex.getMessage(), ex);
        }
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("errorMessage",
                "Upload failed: file exceeds maximum allowed size of 10 MB.");
        return "redirect:/products/create";
    }
}