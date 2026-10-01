package com.onlinelibrary.controller;

import com.onlinelibrary.dto.OrderDTO;
import com.onlinelibrary.service.OrderService;
import com.onlinelibrary.service.OrderService.OrderItemRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 * 
 * 处理订单的创建、查询、删除，以及订单搜索和数据统计功能。
 * 顾客端接口：getOrders（我的订单）、searchOrders（搜索订单）
 * 管理员接口：getAllOrders（全部订单）、searchAllOrders（搜索全部订单）
 * 统计接口：getSalesStatistics（热销榜）、getUserConsumptionStatistics（消费榜）
 *          getUserPurchaseStatistics（个人购买统计）
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<?> getOrders(@RequestParam Long userId) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("获取订单列表失败：" + e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable String orderId) {
        try {
            OrderDTO order = orderService.getOrderById(orderId);
            if (order != null) {
                return ResponseEntity.ok(order);
            } else {
                return ResponseEntity.status(404).body(errorResponse("订单不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("获取订单详情失败：" + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) request.get("items");

            List<OrderItemRequest> items = new ArrayList<>();
            for (Map<String, Object> itemData : itemsData) {
                OrderItemRequest item = new OrderItemRequest();
                item.setBookId(Long.valueOf(itemData.get("bookId").toString()));
                item.setQuantity(Integer.valueOf(itemData.get("quantity").toString()));
                item.setPrice(new BigDecimal(itemData.get("price").toString()));
                if (itemData.get("title") != null) item.setTitle(itemData.get("title").toString());
                if (itemData.get("author") != null) item.setAuthor(itemData.get("author").toString());
                if (itemData.get("image") != null) item.setImage(itemData.get("image").toString());
                items.add(item);
            }

            OrderDTO order = orderService.createOrder(userId, items);
            return ResponseEntity.ok(successResponse("订单创建成功", order));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("创建订单失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable String orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok(successResponse("订单删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("删除订单失败：" + e.getMessage()));
        }
    }

    // ========== 订单搜索 ==========

    @GetMapping("/search")
    public ResponseEntity<?> searchOrders(
            @RequestParam Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String bookName) {
        try {
            List<OrderDTO> orders = orderService.searchOrders(userId, startDate, endDate, bookName);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("搜索订单失败：" + e.getMessage()));
        }
    }

    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<OrderDTO> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("获取所有订单失败：" + e.getMessage()));
        }
    }

    @GetMapping("/admin/search")
    public ResponseEntity<?> searchAllOrders(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String bookName) {
        try {
            List<OrderDTO> orders = orderService.searchAllOrders(startDate, endDate, bookName);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("搜索订单失败：" + e.getMessage()));
        }
    }

    // ========== 统计接口 ==========

    @GetMapping("/statistics/sales")
    public ResponseEntity<?> getSalesStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            List<Map<String, Object>> stats = orderService.getSalesStatistics(startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("获取热销榜失败：" + e.getMessage()));
        }
    }

    @GetMapping("/statistics/consumption")
    public ResponseEntity<?> getUserConsumptionStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            List<Map<String, Object>> stats = orderService.getUserConsumptionStatistics(startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("获取消费榜失败：" + e.getMessage()));
        }
    }

    @GetMapping("/statistics/my-purchase")
    public ResponseEntity<?> getUserPurchaseStatistics(
            @RequestParam Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            List<Map<String, Object>> stats = orderService.getUserPurchaseStatistics(userId, startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("获取个人购买统计失败：" + e.getMessage()));
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
