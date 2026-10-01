package com.onlinelibrary.service;

import com.onlinelibrary.dto.OrderDTO;
import com.onlinelibrary.entity.Book;
import com.onlinelibrary.entity.Order;
import com.onlinelibrary.entity.OrderItem;
import com.onlinelibrary.entity.User;
import com.onlinelibrary.repository.BookRepository;
import com.onlinelibrary.repository.OrderItemRepository;
import com.onlinelibrary.repository.OrderRepository;
import com.onlinelibrary.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单服务测试
 * 
 * 测试订单创建、查询、搜索和统计功能。
 * 使用纯 Mockito 单元测试，模拟数据访问层，聚焦业务逻辑验证。
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void testCreateOrder_ShouldDeductStock() {
        // 准备数据
        Long userId = 1L;
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("测试书籍");
        book.setStock(100);
        book.setPrice(new BigDecimal("50.00"));

        OrderService.OrderItemRequest itemRequest = new OrderService.OrderItemRequest();
        itemRequest.setBookId(bookId);
        itemRequest.setQuantity(2);
        itemRequest.setPrice(new BigDecimal("50.00"));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setUserId(userId);
        savedOrder.setOrderId("20260701001");

        // Mock 行为
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(new ArrayList<>());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        // 执行
        OrderDTO result = orderService.createOrder(userId, Collections.singletonList(itemRequest));

        // 验证：库存从 100 扣减到 98
        assertEquals(98, book.getStock());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testCreateOrder_InsufficientStock_ShouldThrowException() {
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("库存不足的书");
        book.setStock(1);

        OrderService.OrderItemRequest itemRequest = new OrderService.OrderItemRequest();
        itemRequest.setBookId(bookId);
        itemRequest.setQuantity(5);
        itemRequest.setPrice(new BigDecimal("30.00"));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(1L, Collections.singletonList(itemRequest));
        });

        assertTrue(exception.getMessage().contains("库存不足"));
    }

    @Test
    void testGetOrdersByUserId_ShouldReturnOrders() {
        Long userId = 1L;
        Order order1 = new Order();
        order1.setId(1L);
        order1.setUserId(userId);

        when(orderRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Collections.singletonList(order1));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(new ArrayList<>());

        List<OrderDTO> orders = orderService.getOrdersByUserId(userId);

        assertEquals(1, orders.size());
    }

    @Test
    void testSearchOrders_WithDateRange_ShouldFilter() {
        Long userId = 1L;
        when(orderRepository.findByUserIdAndDateRange(
                eq(userId), any(), any()))
                .thenReturn(new ArrayList<>());

        List<OrderDTO> result = orderService.searchOrders(userId, "2026-01-01", "2026-12-31", null);

        assertNotNull(result);
    }

    @Test
    void testGetSalesStatistics_ShouldReturnRanked() {
        Object[] row1 = new Object[]{1L, "书A", "作者A", "image.jpg", 50.00, 100L, 5000.00};
        Object[] row2 = new Object[]{2L, "书B", "作者B", "image2.jpg", 30.00, 80L, 2400.00};

        when(orderRepository.getSalesStatistics(any(), any()))
                .thenReturn(Arrays.asList(row1, row2));

        List<Map<String, Object>> stats = orderService.getSalesStatistics(null, null);

        assertEquals(2, stats.size());
        assertEquals(1, stats.get(0).get("rank"));
        assertEquals("书A", stats.get(0).get("title"));
        assertEquals(100L, stats.get(0).get("totalQuantity"));
    }

    @Test
    void testGetUserConsumptionStatistics_ShouldReturnRanked() {
        Object[] row1 = new Object[]{1L, "用户A", 5L, 1500.00};
        Object[] row2 = new Object[]{2L, "用户B", 3L, 800.00};

        when(orderRepository.getUserConsumptionStatistics(any(), any()))
                .thenReturn(Arrays.asList(row1, row2));

        List<Map<String, Object>> stats = orderService.getUserConsumptionStatistics(null, null);

        assertEquals(2, stats.size());
        assertEquals(1, stats.get(0).get("rank"));
        assertEquals("用户A", stats.get(0).get("username"));
    }

    @Test
    void testGetUserPurchaseStatistics_ShouldReturnSummary() {
        Long userId = 1L;
        Object[] row1 = new Object[]{1L, "书A", "作者A", "img.jpg", 50.00, 5L, 250.00};

        when(orderRepository.getUserPurchaseStatistics(eq(userId), any(), any()))
                .thenReturn(Collections.singletonList(row1));

        List<Map<String, Object>> result = orderService.getUserPurchaseStatistics(userId, null, null);

        assertNotNull(result);
        assertTrue(result.size() > 0);
    }

    @Test
    void testGetOrderById_ShouldReturnOrder() {
        String orderId = "20260701001";
        Order order = new Order();
        order.setId(1L);
        order.setOrderId(orderId);
        order.setUserId(1L);

        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(new ArrayList<>());

        OrderDTO result = orderService.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
    }

    @Test
    void testGetOrderById_NonExisting_ShouldReturnNull() {
        when(orderRepository.findByOrderId("NONEXIST")).thenReturn(Optional.empty());

        OrderDTO result = orderService.getOrderById("NONEXIST");

        assertNull(result);
    }

    @Test
    void testDeleteOrder_ShouldCallDelete() {
        String orderId = "20260701001";
        Order order = new Order();
        order.setId(1L);
        order.setOrderId(orderId);

        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(order));

        orderService.deleteOrder(orderId);

        verify(orderRepository, times(1)).delete(any(Order.class));
    }
}
