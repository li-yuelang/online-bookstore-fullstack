package com.onlinelibrary.service;

import com.onlinelibrary.dto.BookDTO;
import com.onlinelibrary.dto.EntityConverter;
import com.onlinelibrary.entity.Book;
import com.onlinelibrary.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 书籍服务实现类
 * 
 * 实现书籍的查询、搜索、新增、修改和删除功能。
 * 内部使用 BookRepository 操作数据库，对外返回 BookDTO。
 */
@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return EntityConverter.toBookDTOList(books);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> searchBooks(String title) {
        List<Book> books;
        if (title != null && !title.trim().isEmpty()) {
            books = bookRepository.findByTitleContaining(title);
        } else {
            books = bookRepository.findAll();
        }
        return EntityConverter.toBookDTOList(books);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDTO getBookById(Long id) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        return bookOpt.map(EntityConverter::toDTO).orElse(null);
    }

    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        Book book = EntityConverter.toEntity(bookDTO);
        if (book.getStock() == null) book.setStock(0);
        Book saved = bookRepository.save(book);
        return EntityConverter.toDTO(saved);
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        if (!bookOpt.isPresent()) {
            return null;
        }
        Book book = bookOpt.get();
        // 只更新非空字段
        if (bookDTO.getTitle() != null) book.setTitle(bookDTO.getTitle());
        if (bookDTO.getAuthor() != null) book.setAuthor(bookDTO.getAuthor());
        if (bookDTO.getPrice() != null) book.setPrice(bookDTO.getPrice());
        if (bookDTO.getImage() != null) book.setImage(bookDTO.getImage());
        if (bookDTO.getDescription() != null) book.setDescription(bookDTO.getDescription());
        if (bookDTO.getPublisher() != null) book.setPublisher(bookDTO.getPublisher());
        if (bookDTO.getPublishDate() != null) book.setPublishDate(bookDTO.getPublishDate());
        if (bookDTO.getPages() != null) book.setPages(bookDTO.getPages());
        if (bookDTO.getIsbn() != null) book.setIsbn(bookDTO.getIsbn());
        if (bookDTO.getRating() != null) book.setRating(bookDTO.getRating());
        if (bookDTO.getRatingCount() != null) book.setRatingCount(bookDTO.getRatingCount());
        if (bookDTO.getStock() != null) book.setStock(bookDTO.getStock());

        Book saved = bookRepository.save(book);
        return EntityConverter.toDTO(saved);
    }

    @Override
    public void deleteBook(Long id) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        if (bookOpt.isPresent()) {
            bookRepository.deleteById(id);
        }
    }
}
