package com.example.nursery_store.controller;

import com.example.nursery_store.entity.Category;
import com.example.nursery_store.entity.Order;
import com.example.nursery_store.entity.Product;
import com.example.nursery_store.entity.User;
import com.example.nursery_store.service.CategoryService;
import com.example.nursery_store.service.OrderService;
import com.example.nursery_store.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;

    // Admin Dashboard
    @GetMapping("/admin")
    public String adminHome(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null || !"admin".equals(loggedInUser.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("user", loggedInUser);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin";
    }

    // User Order View
    @GetMapping("/user/orders")
    public String getUserOrders(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Order> userOrders = orderService.getOrdersByUserId(loggedInUser.getId());
        model.addAttribute("userOrders", userOrders);
        return "user-orders";
    }

    // Add Product - GET
    @GetMapping("/add-product")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "add-product";
    }

    // Add Product - POST
    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("categoryName") String categoryName,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             Model model) throws IOException {
        Category category = categoryService.findOrCreateCategory(categoryName);
        product.setCategory(category);

        if (!imageFile.isEmpty()) {
            String imageUrl = productService.saveImage(imageFile);
            product.setImage(imageUrl);
        }

        productService.addProduct(product);
        model.addAttribute("message", "Product added successfully!");
        return "redirect:/admin";
    }

    // Edit Product - GET
    @GetMapping("/edit-product/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "edit-product";
    }

    // Edit Product - POST
    @PostMapping("/edit-product/{id}")
    public String editProduct(@PathVariable Long id,
                              @ModelAttribute Product product,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              Model model) throws IOException {
        Product existingProduct = productService.getProductById(id);
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());

        Category category = categoryService.findOrCreateCategory(product.getCategory().getName());
        existingProduct.setCategory(category);

        if (!imageFile.isEmpty()) {
            String imageUrl = productService.saveImage(imageFile);
            existingProduct.setImage(imageUrl);
        }

        productService.updateProduct(existingProduct);
        model.addAttribute("message", "Product updated successfully!");
        return "redirect:/admin";
    }

    // Delete Product
    @PostMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Long id, Model model) {
        productService.deleteProduct(id);
        model.addAttribute("message", "Product deleted successfully!");
        return "redirect:/admin";
    }

    // Product Details
    @GetMapping("/product/{productId}")
    public String getProductDetails(@PathVariable Long productId, Model model) {
        Product product = productService.getProductById(productId);

        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("category", product.getCategory());
            return "product-details";
        } else {
            return "product-not-found";
        }
    }

    // Admin View All Orders
    @GetMapping("/admin/orders")
    public String getAllOrders(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null || !"admin".equals(loggedInUser.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("orders", orderService.getAllOrders());
        return "admin-orders";
    }

    // Admin View User Orders (Optional)
    @GetMapping("/admin/user/orders")
    public String getAdminUserOrders(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null || !"admin".equals(loggedInUser.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("orders", orderService.getOrdersByUserId(loggedInUser.getId()));
        return "admin-user-orders";
    }

    // Unified: Update Delivery Status and Date
    @PostMapping("/admin/update-delivery/{orderId}")
    public String updateDeliveryStatusAndDate(@PathVariable("orderId") Long orderId,
                                              @RequestParam("status") String status,
                                              @RequestParam("deliveryDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date deliveryDate) {
        orderService.updateDeliveryStatusAndDate(orderId, status, deliveryDate);
        return "redirect:/admin";
    }

    // Delete Order
    @PostMapping("/admin/delete-order/{orderId}")
    public String deleteOrder(@PathVariable("orderId") Long orderId) {
        orderService.deleteOrder(orderId);
        return "redirect:/admin";
    }
}
