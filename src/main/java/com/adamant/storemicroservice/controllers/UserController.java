package com.adamant.storemicroservice.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adamant.storemicroservice.models.Product;
import com.adamant.storemicroservice.models.User;
import com.adamant.storemicroservice.models.UserDto;
import com.adamant.storemicroservice.services.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginPage() {
        logger.debug("Showing login page");
        return "users/sign-up";
    }

    @GetMapping("/home")
    public String showHomePage() {
        logger.debug("Showing home page");
        return "users/home";
    }
    

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        logger.debug("Showing register page");
        if (!model.containsAttribute("userDto")) {
            model.addAttribute("userDto", new UserDto());
        }
        return "users/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDto") UserDto userDto, 
                               BindingResult bindingResult,
                               Model model,
                               HttpServletResponse response,
                               RedirectAttributes redirectAttributes) throws IOException {
        logger.info("Processing registration for user: {}", userDto.getUsername());
        
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors: {}", bindingResult.getAllErrors());
            return "users/register";
        }

        // Check if user already exists
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            logger.warn("Username already exists: {}", userDto.getUsername());
            model.addAttribute("error", "Username already exists");
            return "users/register";
        }

        try {
            // Create a new user and save it
            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));

            // Ensure role isn't null - provide a default if needed
            String role = userDto.getRole();
            if (role == null || role.isEmpty()) {
                role = "USER";
                logger.debug("Setting default role to USER");
            }
            user.setRole(role);

            User savedUser = userRepository.save(user);
            logger.info("Successfully created user: {} with ID: {}", savedUser.getUsername(), savedUser.getId());
            
            // For AJAX requests, respond with a JSON success status
            if (isAjaxRequest(response)) {
                // The response will be handled by JavaScript to show the popup
                return "redirect:/login";
            }
            
            // For regular form submissions, use redirectAttributes
            redirectAttributes.addFlashAttribute("successMessage", "User registered successfully");
            return "redirect:/register?status=success";

        } catch (Exception e) {
            logger.error("Error saving user: {}", e.getMessage(), e);
            model.addAttribute("error", "An error occurred while saving the user: " + e.getMessage());
            return "users/register";
        }
    }
    
    /**
     * Helper method to check if request is an AJAX request
     */
    private boolean isAjaxRequest(HttpServletResponse response) {
        return response.getContentType() != null && 
               response.getContentType().toLowerCase().contains("application/json");
    }
}