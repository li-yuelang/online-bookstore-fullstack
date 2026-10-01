package com.onlinelibrary.repository;

import com.onlinelibrary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 * 
 * 提供用户的 CRUD 操作，以及按用户名查找、检查用户名是否存在等功能。
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findByIdNot(Long id);
}