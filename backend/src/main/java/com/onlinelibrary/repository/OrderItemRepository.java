package com.onlinelibrary.repository;

import com.onlinelibrary.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单项数据访问接口
 * 
 * 提供订单项的基本查询，支持按订单 ID 查询订单项列表。
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderId(Long orderId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.orderId = :orderId")
    List<OrderItem> getOrderItemsWithBookInfo(@Param("orderId") Long orderId);
}
