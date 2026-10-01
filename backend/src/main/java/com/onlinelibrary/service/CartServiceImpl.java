package com.onlinelibrary.service;

import com.onlinelibrary.dto.CartItemDTO;
import com.onlinelibrary.dto.EntityConverter;
import com.onlinelibrary.entity.CartItem;
import com.onlinelibrary.entity.Book;
import com.onlinelibrary.repository.BookRepository;
import com.onlinelibrary.repository.CartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 购物车服务实现类
 * 
 * 实现购物车的完整业务逻辑：
 * - 添加商品时，如果已有该书籍则增加数量，否则新建记录
 * - 查询购物车时，自动关联书籍详情信息
 * - 更新数量时，如果数量 <= 0 则自动删除该记录
 * - 返回 DTO 而非 Entity，屏蔽底层数据来源
 */
@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    public CartServiceImpl(CartItemRepository cartItemRepository, BookRepository bookRepository) {
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * 填充购物车项的书籍详情（从一个数据源获取并设置到 Entity）
     */
    private void enrichCartItem(CartItem item) {
        Book book = bookRepository.findById(item.getBookId()).orElse(null);
        if (book != null) {
            item.setBook(book);
            item.setPrice(book.getPrice());
            item.setTitle(book.getTitle());
            item.setAuthor(book.getAuthor());
            item.setImage(book.getImage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemDTO> getCartByUserId(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        cartItems.forEach(this::enrichCartItem);
        return EntityConverter.toCartItemDTOList(cartItems);
    }

    @Override
    public CartItemDTO addToCart(Long userId, Long bookId, Integer quantity) {
        CartItem existingItem = cartItemRepository.findByUserIdAndBookId(userId, bookId);

        CartItem savedItem;
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            savedItem = cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setBookId(bookId);
            newItem.setQuantity(quantity);
            savedItem = cartItemRepository.save(newItem);
        }

        enrichCartItem(savedItem);
        return EntityConverter.toDTO(savedItem);
    }

    @Override
    public void updateQuantity(Long userId, Long bookId, Integer quantity) {
        CartItem item = cartItemRepository.findByUserIdAndBookId(userId, bookId);
        if (item != null) {
            if (quantity <= 0) {
                cartItemRepository.delete(item);
            } else {
                item.setQuantity(quantity);
                cartItemRepository.save(item);
            }
        }
    }

    @Override
    public void removeFromCart(Long userId, Long bookId) {
        cartItemRepository.deleteByUserIdAndBookId(userId, bookId);
    }

    @Override
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartCount(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
