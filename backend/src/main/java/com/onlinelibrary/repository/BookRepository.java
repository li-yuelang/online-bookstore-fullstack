package com.onlinelibrary.repository;

import com.onlinelibrary.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 书籍数据访问接口
 * 
 * 提供书籍的 CRUD 操作，以及按书名模糊搜索功能。
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContaining(String title);
}
