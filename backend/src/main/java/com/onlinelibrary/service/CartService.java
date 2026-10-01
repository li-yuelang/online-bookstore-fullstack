package com.onlinelibrary.service;

import com.onlinelibrary.dto.CartItemDTO;
import java.util.List;

/**
 * 购物车服务接口
 * 
 * 定义购物车的核心业务操作：获取购物车、添加商品、修改数量、删除商品、清空购物车。
 * 返回 DTO 而非 Entity，屏蔽底层数据来源。
 */
public interface CartService {
    
    List<CartItemDTO> getCartByUserId(Long userId);
    
    CartItemDTO addToCart(Long userId, Long bookId, Integer quantity);
    
    void updateQuantity(Long userId, Long bookId, Integer quantity);
    
    void removeFromCart(Long userId, Long bookId);
    
    void clearCart(Long userId);
    
    int getCartCount(Long userId);
}
