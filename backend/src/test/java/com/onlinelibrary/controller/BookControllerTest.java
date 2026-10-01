package com.onlinelibrary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinelibrary.entity.Book;
import com.onlinelibrary.repository.BookRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 书籍控制器测试
 * 
 * 测试书籍列表获取、详情查询、搜索，以及管理员的 CRUD 操作。
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    private static final String TEST_TITLE = "测试书籍_" + System.currentTimeMillis();

    @Test
    @org.junit.jupiter.api.Order(1)
    void testGetAllBooks_ShouldReturnList() throws Exception {
        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void testGetBookById_ExistingBook_ShouldReturnBook() throws Exception {
        // 使用数据库中已有的第 1 本书
        mockMvc.perform(get("/api/v1/book/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").isString());
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testGetBookById_NonExistingBook_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/book/99999"))
                .andExpect(status().is(404));
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void testSearchBooks_ByTitle_ShouldReturnFiltered() throws Exception {
        mockMvc.perform(get("/api/v1/books/search")
                .param("title", "JavaScript"))
                .andExpect(status().isOk());
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void testAdminCreateBook_ShouldSucceed() throws Exception {
        Map<String, Object> newBook = new HashMap<>();
        newBook.put("title", TEST_TITLE);
        newBook.put("author", "测试作者");
        newBook.put("price", 29.99);
        newBook.put("stock", 100);
        newBook.put("isbn", "978-TEST-001");
        newBook.put("publisher", "测试出版社");
        newBook.put("description", "这是一本测试书籍");
        newBook.put("rating", 4.5);
        newBook.put("ratingCount", 10);

        mockMvc.perform(post("/api/v1/admin/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("书籍添加成功"));
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void testAdminUpdateBook_ShouldSucceed() throws Exception {
        // 先创建再更新
        Book testBook = new Book();
        testBook.setTitle(TEST_TITLE + "_update");
        testBook.setAuthor("原作者");
        testBook.setPrice(new BigDecimal("39.99"));
        testBook.setStock(50);
        testBook.setRating(4.0);
        testBook.setRatingCount(5);
        Book saved = bookRepository.save(testBook);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("title", TEST_TITLE + "_updated");
        updateData.put("author", "新作者");
        updateData.put("price", 49.99);
        updateData.put("stock", 80);

        mockMvc.perform(put("/api/v1/admin/books/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("书籍更新成功"));

        // 清理
        bookRepository.deleteById(saved.getId());
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    void testAdminDeleteBook_ShouldSucceed() throws Exception {
        // 先创建一本用于删除的测试书
        Book testBook = new Book();
        testBook.setTitle(TEST_TITLE + "_delete");
        testBook.setAuthor("删除测试");
        testBook.setPrice(new BigDecimal("19.99"));
        testBook.setStock(10);
        testBook.setRating(3.0);
        testBook.setRatingCount(1);
        Book saved = bookRepository.save(testBook);

        mockMvc.perform(delete("/api/v1/admin/books/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("书籍删除成功"));
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void testAdminDeleteBook_NonExisting_ShouldFail() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/books/99999"))
                .andExpect(status().isNotFound());
    }
}
