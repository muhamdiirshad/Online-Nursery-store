package com.example.nursery_store.controller;


import com.example.nursery_store.entity.Cart;
import com.example.nursery_store.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId, HttpSession session) {
        cartService.addToCart(productId, session);
        return "redirect:/cart/view";
    }

    @PostMapping("/remove")
    public String removeFromCartAndSave(@RequestParam("productId") Long productId, HttpSession session) {
        cartService.removeFromCartAndSave(productId, session);
        return "redirect:/cart/view";
    }

    @GetMapping("/view")
    public String viewCart(Model model, HttpSession session) {
        Cart cart = cartService.getCart(session);
        model.addAttribute("cart", cart);
        model.addAttribute("total", cart.getTotal());
        return "cart";
    }

}
