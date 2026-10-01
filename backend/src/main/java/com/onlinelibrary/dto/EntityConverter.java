package com.onlinelibrary.dto;

import com.onlinelibrary.entity.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Entity-DTO 转换器
 * 
 * 负责在 JPA Entity 和 DTO 之间进行双向转换。
 * 通过此层屏蔽底层数据来源，为未来接入多种异构数据库做准备。
 * 
 * 使用规范：
 * - toDTO(Entity) → 将 Entity 转换为 DTO（输出到 Controller）
 * - toEntity(DTO) → 将 DTO 转换为 Entity（写入到 Repository）
 * 
 * 注意：User → UserDTO 时不转换密码字段，确保信息安全。
 */
public class EntityConverter {

    // ========== Book 转换 ==========

    public static BookDTO toDTO(Book book) {
        if (book == null) return null;
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPrice(book.getPrice());
        dto.setImage(book.getImage());
        dto.setRating(book.getRating());
        dto.setRatingCount(book.getRatingCount());
        dto.setPublisher(book.getPublisher());
        dto.setPublishDate(book.getPublishDate());
        dto.setPages(book.getPages());
        dto.setIsbn(book.getIsbn());
        dto.setStock(book.getStock());
        dto.setDescription(book.getDescription());
        if (book.getReviews() != null) {
            dto.setReviews(book.getReviews().stream()
                .map(EntityConverter::toDTO)
                .collect(Collectors.toList()));
        }
        return dto;
    }

    public static Book toEntity(BookDTO dto) {
        if (dto == null) return null;
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPrice(dto.getPrice());
        book.setImage(dto.getImage());
        book.setRating(dto.getRating());
        book.setRatingCount(dto.getRatingCount());
        book.setPublisher(dto.getPublisher());
        book.setPublishDate(dto.getPublishDate());
        book.setPages(dto.getPages());
        book.setIsbn(dto.getIsbn());
        book.setStock(dto.getStock());
        book.setDescription(dto.getDescription());
        return book;
    }

    public static List<BookDTO> toBookDTOList(List<Book> books) {
        if (books == null) return Collections.emptyList();
        return books.stream().map(EntityConverter::toDTO).collect(Collectors.toList());
    }

    // ========== Review 转换 ==========

    public static ReviewDTO toDTO(Review review) {
        if (review == null) return null;
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        if (review.getBook() != null) {
            dto.setBookId(review.getBook().getId());
        }
        dto.setName(review.getName());
        // Review.rating 是 String 类型（如 "4.5"），需转为 Double
        try {
            dto.setRating(Double.parseDouble(review.getRating()));
        } catch (NumberFormatException e) {
            dto.setRating(0.0);
        }
        dto.setDate(review.getDate());
        dto.setContent(review.getContent());
        return dto;
    }

    // ========== User 转换（不转换密码） ==========

    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setEnabled(user.getEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setEnabled(dto.getEnabled());
        return user;
    }

    public static List<UserDTO> toUserDTOList(List<User> users) {
        if (users == null) return Collections.emptyList();
        return users.stream().map(EntityConverter::toDTO).collect(Collectors.toList());
    }

    // ========== CartItem 转换 ==========

    public static CartItemDTO toDTO(CartItem cartItem) {
        if (cartItem == null) return null;
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setUserId(cartItem.getUserId());
        dto.setBookId(cartItem.getBookId());
        dto.setQuantity(cartItem.getQuantity());
        dto.setCreatedAt(cartItem.getCreatedAt());
        dto.setPrice(cartItem.getPrice());
        dto.setTitle(cartItem.getTitle());
        dto.setAuthor(cartItem.getAuthor());
        dto.setImage(cartItem.getImage());
        return dto;
    }

    public static List<CartItemDTO> toCartItemDTOList(List<CartItem> items) {
        if (items == null) return Collections.emptyList();
        return items.stream().map(EntityConverter::toDTO).collect(Collectors.toList());
    }

    // ========== OrderItem 转换 ==========

    public static OrderItemDTO toDTO(OrderItem orderItem) {
        if (orderItem == null) return null;
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setOrderId(orderItem.getOrderId());
        dto.setBookId(orderItem.getBookId());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        dto.setTitle(orderItem.getTitle());
        dto.setAuthor(orderItem.getAuthor());
        dto.setImage(orderItem.getImage());
        return dto;
    }

    public static List<OrderItemDTO> toOrderItemDTOList(List<OrderItem> items) {
        if (items == null) return Collections.emptyList();
        return items.stream().map(EntityConverter::toDTO).collect(Collectors.toList());
    }

    // ========== Order 转换 ==========

    public static OrderDTO toDTO(Order order) {
        if (order == null) return null;
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setOrderId(order.getOrderId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUsername(order.getUsername());
        dto.setItems(toOrderItemDTOList(order.getItems()));
        return dto;
    }

    public static List<OrderDTO> toOrderDTOList(List<Order> orders) {
        if (orders == null) return Collections.emptyList();
        return orders.stream().map(EntityConverter::toDTO).collect(Collectors.toList());
    }
}
