package com.onlinelibrary.service;

import com.onlinelibrary.dto.CartItemDTO;
import com.onlinelibrary.entity.Book;
import com.onlinelibrary.entity.CartItem;
import com.onlinelibrary.repository.BookRepository;
import com.onlinelibrary.repository.CartItemRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 购物车服务测试
 * 
 * 测试购物车的添加、查询、更新数量和删除功能。
 */
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void cleanup() throws Exception {
        closeable.close();
    }

    @Test
    void testAddToCart_NewItem_ShouldCreate() {
        Long userId = 1L;
        Long bookId = 1L;

        Book book = new Book();
        book.setId(bookId);
        book.setTitle("测试书");
        book.setPrice(new BigDecimal("50.00"));

        when(cartItemRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(null);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> {
            CartItem item = inv.getArgument(0);
            item.setId(1L);
            return item;
        });

        CartItemDTO result = cartService.addToCart(userId, bookId, 1);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(bookId, result.getBookId());
        assertEquals(1, result.getQuantity());
    }

    @Test
    void testAddToCart_ExistingItem_ShouldIncreaseQuantity() {
        Long userId = 1L;
        Long bookId = 1L;

        CartItem existing = new CartItem();
        existing.setId(1L);
        existing.setUserId(userId);
        existing.setBookId(bookId);
        existing.setQuantity(2);

        Book book = new Book();
        book.setId(bookId);
        book.setTitle("测试书");
        book.setPrice(new BigDecimal("50.00"));

        when(cartItemRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(existing);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        CartItemDTO result = cartService.addToCart(userId, bookId, 3);

        assertEquals(5, result.getQuantity());
    }

    @Test
    void testGetCartByUserId_ShouldReturnItems() {
        Long userId = 1L;

        CartItem item1 = new CartItem();
        item1.setId(1L);
        item1.setUserId(userId);
        item1.setBookId(1L);
        item1.setQuantity(2);

        when(cartItemRepository.findByUserId(userId)).thenReturn(Arrays.asList(item1));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(new Book()));

        List<CartItemDTO> cart = cartService.getCartByUserId(userId);

        assertEquals(1, cart.size());
    }

    @Test
    void testRemoveFromCart_ShouldDelete() {
        Long userId = 1L;
        Long bookId = 1L;

        cartService.removeFromCart(userId, bookId);

        verify(cartItemRepository, times(1)).deleteByUserIdAndBookId(userId, bookId);
    }

    @Test
    void testClearCart_ShouldDeleteAll() {
        Long userId = 1L;

        cartService.clearCart(userId);

        verify(cartItemRepository, times(1)).deleteByUserId(userId);
    }

    @Test
    void testUpdateQuantity_ShouldUpdate() {
        Long userId = 1L;
        Long bookId = 1L;

        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setBookId(bookId);
        item.setQuantity(1);

        when(cartItemRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(item);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));

        cartService.updateQuantity(userId, bookId, 5);

        assertEquals(5, item.getQuantity());
    }

    @Test
    void testGetCartCount_ShouldReturnTotalQuantity() {
        Long userId = 1L;

        CartItem item1 = new CartItem();
        item1.setQuantity(2);
        CartItem item2 = new CartItem();
        item2.setQuantity(3);

        when(cartItemRepository.findByUserId(userId)).thenReturn(Arrays.asList(item1, item2));

        int count = cartService.getCartCount(userId);

        assertEquals(5, count);
    }
}
