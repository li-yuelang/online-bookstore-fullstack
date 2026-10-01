package com.onlinelibrary.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车项数据传输对象
 * 
 * 屏蔽底层数据来源，与 JPA Entity 解耦。
 * 包含购物车中的书籍信息和关联的书籍详情。
 */
public class CartItemDTO {

    private Long id;
    private Long userId;
    private Long bookId;
    private Integer quantity;
    private LocalDateTime createdAt;

    // 关联的书籍详情（由其他数据源填充）
    private BigDecimal price;
    private String title;
    private String author;
    private String image;

    public CartItemDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
}
