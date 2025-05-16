// package com.adamant.storemicroservice.controllers;

// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;

// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpSession;

// @Controller
// public class SessionController {

//     @GetMapping("/logout")
//     public String logout(HttpServletRequest request) {
//         HttpSession session = request.getSession(false);
//         if (session != null) {
//             session.invalidate();  // This invalidates the session
//             System.out.println(">>> Session destroyed at " + new java.util.Date());
//         }
//         return "redirect:/login"; // Or wherever you want to redirect
//     }
    

// }