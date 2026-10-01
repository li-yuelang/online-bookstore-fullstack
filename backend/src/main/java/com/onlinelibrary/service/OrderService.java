package com.onlinelibrary.service;

import com.onlinelibrary.dto.OrderDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 * 
 * 定义订单的核心业务操作和统计功能：
 * - 订单的创建、查询、删除
 * - 订单搜索过滤（按时间范围、书籍名称）
 * - 管理员统计：热销榜、消费榜
 * - 顾客统计：个人购买情况
 * - 返回 DTO 而非 Entity，屏蔽底层数据来源
 * 
 * OrderItemRequest 为内部类，用于封装创建订单时传入的每本书的信息。
 */
public interface OrderService {

    List<OrderDTO> getOrdersByUserId(Long userId);

    List<OrderDTO> getAllOrders();

    List<OrderDTO> searchOrders(Long userId, String startDate, String endDate, String bookName);

    List<OrderDTO> searchAllOrders(String startDate, String endDate, String bookName);

    OrderDTO getOrderById(String orderId);

    OrderDTO createOrder(Long userId, List<OrderItemRequest> items);

    void deleteOrder(String orderId);

    // 统计接口
    List<Map<String, Object>> getSalesStatistics(String startDate, String endDate);

    List<Map<String, Object>> getUserConsumptionStatistics(String startDate, String endDate);

    List<Map<String, Object>> getUserPurchaseStatistics(Long userId, String startDate, String endDate);

    class OrderItemRequest {
        private Long bookId;
        private Integer quantity;
        private String title;
        private String author;
        private String image;
        private BigDecimal price;

        public Long getBookId() {
            return bookId;
        }

        public void setBookId(Long bookId) {
            this.bookId = bookId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
}
