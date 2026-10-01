package com.onlinelibrary.service;

import com.onlinelibrary.dto.EntityConverter;
import com.onlinelibrary.dto.OrderDTO;
import com.onlinelibrary.entity.Book;
import com.onlinelibrary.entity.Order;
import com.onlinelibrary.entity.OrderItem;
import com.onlinelibrary.entity.User;
import com.onlinelibrary.repository.BookRepository;
import com.onlinelibrary.repository.OrderItemRepository;
import com.onlinelibrary.repository.OrderRepository;
import com.onlinelibrary.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 * 
 * 实现订单核心业务逻辑：
 * - 创建订单时同步扣减书籍库存，若库存不足则抛出异常
 * - 支持按时间范围和书籍名称过滤订单
 * - 使用 Native SQL 查询实现热销榜、消费榜和个人购买统计
 * - 订单创建后自动生成唯一订单号（日期+随机数）
 * - 返回 DTO 而非 Entity，屏蔽底层数据来源
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           BookRepository bookRepository,
                           UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * 填充订单的额外信息（订单项详情、用户名）
     * 此方法操作 Entity 层，后续可改为从不同的数据源获取
     */
    private void enrichOrder(Order order) {
        if (order == null) return;
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        items.forEach(item -> {
            Book book = bookRepository.findById(item.getBookId()).orElse(null);
            if (book != null) {
                item.setBook(book);
                item.setTitle(book.getTitle());
                item.setAuthor(book.getAuthor());
                item.setImage(book.getImage());
            }
        });
        order.setItems(items);

        User user = userRepository.findById(order.getUserId()).orElse(null);
        if (user != null) {
            order.setUsername(user.getUsername());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        orders.forEach(this::enrichOrder);
        return EntityConverter.toOrderDTOList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        orders.forEach(this::enrichOrder);
        return EntityConverter.toOrderDTOList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> searchOrders(Long userId, String startDate, String endDate, String bookName) {
        LocalDateTime start = parseStartDate(startDate);
        LocalDateTime end = parseEndDate(endDate);

        List<Order> orders;
        if (start != null || end != null) {
            orders = orderRepository.findByUserIdAndDateRange(userId, start, end);
        } else {
            orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }

        orders.forEach(this::enrichOrder);

        // 如果指定了书籍名称，过滤
        if (bookName != null && !bookName.trim().isEmpty()) {
            String finalBookName = bookName.toLowerCase();
            orders = orders.stream()
                .filter(order -> order.getItems().stream()
                    .anyMatch(item -> item.getTitle() != null &&
                        item.getTitle().toLowerCase().contains(finalBookName)))
                .collect(Collectors.toList());
        }

        return EntityConverter.toOrderDTOList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> searchAllOrders(String startDate, String endDate, String bookName) {
        LocalDateTime start = parseStartDate(startDate);
        LocalDateTime end = parseEndDate(endDate);

        List<Order> orders;
        if (start != null || end != null) {
            orders = orderRepository.findByDateRange(start, end);
        } else {
            orders = orderRepository.findAllByOrderByCreatedAtDesc();
        }

        orders.forEach(this::enrichOrder);

        // 如果指定了书籍名称，过滤
        if (bookName != null && !bookName.trim().isEmpty()) {
            String finalBookName = bookName.toLowerCase();
            orders = orders.stream()
                .filter(order -> order.getItems().stream()
                    .anyMatch(item -> item.getTitle() != null &&
                        item.getTitle().toLowerCase().contains(finalBookName)))
                .collect(Collectors.toList());
        }

        return EntityConverter.toOrderDTOList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(String orderId) {
        Order order = orderRepository.findByOrderId(orderId).orElse(null);
        if (order != null) {
            enrichOrder(order);
        }
        return EntityConverter.toDTO(order);
    }

    @Override
    public OrderDTO createOrder(Long userId, List<OrderItemRequest> items) {
        String orderId = generateOrderId();
        BigDecimal totalAmount = BigDecimal.ZERO;

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderId(orderId);

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : items) {
            // 扣减库存
            Optional<Book> bookOpt = bookRepository.findById(itemRequest.getBookId());
            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                int newStock = book.getStock() - itemRequest.getQuantity();
                if (newStock < 0) {
                    throw new RuntimeException("书籍《" + book.getTitle() + "》库存不足");
                }
                book.setStock(newStock);
                bookRepository.save(book);
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setBookId(itemRequest.getBookId());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(itemRequest.getPrice());

            totalAmount = totalAmount.add(
                itemRequest.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
            );

            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(order.getId());
            orderItemRepository.save(orderItem);
        }

        Order savedOrder = orderRepository.findById(order.getId()).orElse(order);
        enrichOrder(savedOrder);

        return EntityConverter.toDTO(savedOrder);
    }

    @Override
    public void deleteOrder(String orderId) {
        Order order = orderRepository.findByOrderId(orderId).orElse(null);
        if (order != null) {
            orderRepository.delete(order);
        }
    }

    // ========== 统计接口 ==========

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSalesStatistics(String startDate, String endDate) {
        List<Object[]> results = orderRepository.getSalesStatistics(startDate, endDate);
        List<Map<String, Object>> list = new ArrayList<>();
        int rank = 1;
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("rank", rank++);
            item.put("bookId", row[0]);
            item.put("title", row[1]);
            item.put("author", row[2]);
            item.put("image", row[3]);
            item.put("price", row[4]);
            item.put("totalQuantity", row[5]);
            item.put("totalAmount", row[6]);
            list.add(item);
        }
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserConsumptionStatistics(String startDate, String endDate) {
        List<Object[]> results = orderRepository.getUserConsumptionStatistics(startDate, endDate);
        List<Map<String, Object>> list = new ArrayList<>();
        int rank = 1;
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("rank", rank++);
            item.put("userId", row[0]);
            item.put("username", row[1]);
            item.put("orderCount", row[2]);
            item.put("totalSpent", row[3]);
            list.add(item);
        }
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserPurchaseStatistics(Long userId, String startDate, String endDate) {
        List<Object[]> results = orderRepository.getUserPurchaseStatistics(userId, startDate, endDate);
        List<Map<String, Object>> list = new ArrayList<>();
        int totalBooks = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("bookId", row[0]);
            item.put("title", row[1]);
            item.put("author", row[2]);
            item.put("image", row[3]);
            item.put("price", row[4]);
            item.put("totalQuantity", row[5]);
            item.put("totalAmount", row[6]);
            list.add(item);

            totalBooks += ((Number) row[5]).intValue();
            totalAmount = totalAmount.add(new BigDecimal(row[6].toString()));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items", list);
        result.put("totalBooks", totalBooks);
        result.put("totalAmount", totalAmount);
        result.put("itemCount", list.size());

        return Collections.singletonList(result);
    }

    // ========== 工具方法 ==========

    private LocalDateTime parseStartDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return date.atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseEndDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return date.atTime(LocalTime.MAX);
        } catch (Exception e) {
            return null;
        }
    }

    private String generateOrderId() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePrefix = now.format(formatter);

        Random random = new Random();
        String randomSuffix = String.format("%03d", random.nextInt(1000));

        return datePrefix + randomSuffix;
    }
}
