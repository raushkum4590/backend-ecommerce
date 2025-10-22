package com.ecommerce.cart.controller;

import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.service.CartService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/user/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        return cartService.getCartByUserId(userId);
    }

    @PostMapping("/add")
    public Cart addToCart(@RequestBody AddToCartRequest request) {
        return cartService.addItemToCart(
            request.getUserId(),
            request.getProductId(),
            request.getQuantity(),
            request.getPrice()
        );
    }

    @DeleteMapping("/remove/{userId}/{productId}")
    public Cart removeFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        return cartService.removeItemFromCart(userId, productId);
    }

    @DeleteMapping("/clear/{userId}")
    public Cart clearCart(@PathVariable Long userId) {
        return cartService.clearCart(userId);
    }
}

@Data
class AddToCartRequest {
    private Long userId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.ecommerce</groupId>
        <artifactId>ecommerce-microservices</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>cart-service</artifactId>
    <name>Cart Service</name>

    <dependencies>
        <dependency>
            <groupId>com.ecommerce</groupId>
            <artifactId>common</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>

</project>

