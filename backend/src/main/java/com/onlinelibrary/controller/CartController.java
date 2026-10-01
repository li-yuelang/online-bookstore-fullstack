package com.onlinelibrary.controller;

import com.onlinelibrary.dto.CartItemDTO;
import com.onlinelibrary.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 * 
 * 处理购物车的增删改查操作。
 * 所有接口均需传入 userId 参数来标识用户。
 */
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<?> getCart(@RequestParam Long userId) {
        try {
            List<CartItemDTO> cartItems = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("获取购物车失败：" + e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long bookId = Long.valueOf(request.get("bookId").toString());
            Integer quantity = request.get("quantity") != null ? 
                Integer.valueOf(request.get("quantity").toString()) : 1;

            CartItemDTO item = cartService.addToCart(userId, bookId, quantity);
            return ResponseEntity.ok(successResponse("添加成功", item));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("添加购物车失败：" + e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateQuantity(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long bookId = Long.valueOf(request.get("bookId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());

            cartService.updateQuantity(userId, bookId, quantity);
            return ResponseEntity.ok(successResponse("更新成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("更新购物车失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long bookId = Long.valueOf(request.get("bookId").toString());

            cartService.removeFromCart(userId, bookId);
            return ResponseEntity.ok(successResponse("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("删除购物车失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@RequestParam Long userId) {
        try {
            cartService.clearCart(userId);
            return ResponseEntity.ok(successResponse("清空购物车成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("清空购物车失败：" + e.getMessage()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getCartCount(@RequestParam Long userId) {
        try {
            int count = cartService.getCartCount(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", count);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("获取购物车数量失败：" + e.getMessage()));
        }
    }

    private Map<String, Object> successResponse(String message, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        result.put("data", data);
        return result;
    }

    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        return result;
    }
}
