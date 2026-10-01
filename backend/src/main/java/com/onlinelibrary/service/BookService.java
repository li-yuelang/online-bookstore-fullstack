package com.onlinelibrary.service;

import com.onlinelibrary.dto.BookDTO;
import java.util.List;

/**
 * 书籍服务接口
 * 
 * 定义书籍的核心业务操作，返回 DTO 而非 Entity，
 * 屏蔽底层数据来源，为接入多种异构数据库做准备。
 */
public interface BookService {

    List<BookDTO> getAllBooks();

    List<BookDTO> searchBooks(String title);

    BookDTO getBookById(Long id);

    BookDTO createBook(BookDTO bookDTO);

    BookDTO updateBook(Long id, BookDTO bookDTO);

    void deleteBook(Long id);
}
