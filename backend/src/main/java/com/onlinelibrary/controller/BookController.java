package com.onlinelibrary.controller;

import com.onlinelibrary.dto.BookDTO;
import com.onlinelibrary.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 书籍控制器
 * 
 * 处理书籍的浏览和管理功能。
 * 游客端接口：getAllBooks（获取列表）、searchBooks（搜索）、getBookById（详情）
 * 管理员接口：createBook（添加）、updateBook（修改）、deleteBook（删除）
 * 使用 DTO 返回数据，屏蔽底层数据来源。
 */
@RestController
@RequestMapping("/api/v1")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/books/search")
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam(required = false) String title) {
        List<BookDTO> books = bookService.searchBooks(title);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        BookDTO book = bookService.getBookById(id);
        if (book != null) {
            return ResponseEntity.ok(book);
        }
        return ResponseEntity.status(404).body(errorResponse("书籍不存在"));
    }

    // ========== 管理员：书籍管理CRUD ==========

    @PostMapping("/admin/books")
    public ResponseEntity<?> createBook(@RequestBody BookDTO bookDTO) {
        try {
            BookDTO saved = bookService.createBook(bookDTO);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "书籍添加成功");
            result.put("data", saved);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("添加书籍失败：" + e.getMessage()));
        }
    }

    @PutMapping("/admin/books/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        try {
            BookDTO saved = bookService.updateBook(id, bookDTO);
            if (saved == null) {
                return ResponseEntity.status(404).body(errorResponse("书籍不存在"));
            }
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "书籍更新成功");
            result.put("data", saved);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("更新书籍失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/admin/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            BookDTO book = bookService.getBookById(id);
            if (book == null) {
                return ResponseEntity.status(404).body(errorResponse("书籍不存在"));
            }
            bookService.deleteBook(id);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "书籍删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("删除书籍失败：" + e.getMessage()));
        }
    }

    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", message);
        return map;
    }
}
