package com.onlinelibrary.repository;

import com.onlinelibrary.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 购物车项数据访问接口
 * 
 * 提供购物车项的增删改查，支持按用户查询、按用户+书籍查询、按用户删除等操作。
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByUserId(Long userId);
    
    CartItem findByUserIdAndBookId(Long userId, Long bookId);
    
    @Query("SELECT ci FROM CartItem ci WHERE ci.userId = :userId")
    List<CartItem> getCartItemsWithBookInfo(@Param("userId") Long userId);
    
    void deleteByUserIdAndBookId(Long userId, Long bookId);
    
    void deleteByUserId(Long userId);
}
