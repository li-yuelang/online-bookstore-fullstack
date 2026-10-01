package com.onlinelibrary.repository;

import com.onlinelibrary.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问接口
 * 
 * 提供订单和订单项的查询、搜索、统计功能，包括：
 * - 按用户查询订单
 * - 按时间范围 + 用户查询订单（搜索功能）
 * - 热销榜统计（按书籍销量排序）
 * - 消费榜统计（按用户消费金额排序）
 * - 个人购买统计
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Order> findByOrderId(String orderId);

    List<Order> findAllByOrderByCreatedAtDesc();

    @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.createdAt DESC")
    List<Order> getOrdersWithItems(@Param("userId") Long userId);

    // 顾客：根据用户ID和时间范围查询订单
    @Query("SELECT o FROM Order o WHERE o.userId = :userId " +
           "AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR o.createdAt <= :endDate) " +
           "ORDER BY o.createdAt DESC")
    List<Order> findByUserIdAndDateRange(@Param("userId") Long userId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    // 管理员：根据时间范围查询所有订单
    @Query("SELECT o FROM Order o " +
           "WHERE (:startDate IS NULL OR o.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR o.createdAt <= :endDate) " +
           "ORDER BY o.createdAt DESC")
    List<Order> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    // 统计：指定时间范围内各书籍的销量（热销榜）
    @Query(value = "SELECT oi.book_id, b.title, b.author, b.image, b.price, " +
           "SUM(oi.quantity) as total_quantity, SUM(oi.quantity * oi.price) as total_amount " +
           "FROM order_items oi " +
           "JOIN orders o ON oi.order_id = o.id " +
           "JOIN books b ON oi.book_id = b.id " +
           "WHERE (:startDate IS NULL OR o.created_at >= :startDate) " +
           "AND (:endDate IS NULL OR o.created_at <= :endDate) " +
           "GROUP BY oi.book_id " +
           "ORDER BY total_quantity DESC", nativeQuery = true)
    List<Object[]> getSalesStatistics(@Param("startDate") String startDate,
                                      @Param("endDate") String endDate);

    // 统计：指定时间范围内每个用户的累计消费（消费榜）
    @Query(value = "SELECT o.user_id, u.username, " +
           "COUNT(DISTINCT o.id) as order_count, SUM(o.total_amount) as total_spent " +
           "FROM orders o " +
           "JOIN users u ON o.user_id = u.id " +
           "WHERE (:startDate IS NULL OR o.created_at >= :startDate) " +
           "AND (:endDate IS NULL OR o.created_at <= :endDate) " +
           "GROUP BY o.user_id " +
           "ORDER BY total_spent DESC", nativeQuery = true)
    List<Object[]> getUserConsumptionStatistics(@Param("startDate") String startDate,
                                                 @Param("endDate") String endDate);

    // 统计：顾客个人的购买情况
    @Query(value = "SELECT oi.book_id, b.title, b.author, b.image, b.price, " +
           "SUM(oi.quantity) as total_quantity, SUM(oi.quantity * oi.price) as total_amount " +
           "FROM order_items oi " +
           "JOIN orders o ON oi.order_id = o.id " +
           "JOIN books b ON oi.book_id = b.id " +
           "WHERE o.user_id = :userId " +
           "AND (:startDate IS NULL OR o.created_at >= :startDate) " +
           "AND (:endDate IS NULL OR o.created_at <= :endDate) " +
           "GROUP BY oi.book_id " +
           "ORDER BY total_quantity DESC", nativeQuery = true)
    List<Object[]> getUserPurchaseStatistics(@Param("userId") Long userId,
                                              @Param("startDate") String startDate,
                                              @Param("endDate") String endDate);
}
