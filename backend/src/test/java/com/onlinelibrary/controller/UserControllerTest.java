package com.onlinelibrary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinelibrary.entity.User;
import com.onlinelibrary.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户控制器测试
 * 
 * 测试用户注册、登录、查询和禁用/解禁功能。
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private static final String TEST_USER = "u" + (System.currentTimeMillis() % 1000000);
    private static final String TEST_PASSWORD = "test123456";
    private static final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setup() {
        // 清理可能存在的测试用户
        userRepository.findByUsername(TEST_USER).ifPresent(user -> 
            userRepository.delete(user)
        );
    }

    /**
     * 创建注册请求体
     */
    private Map<String, String> createRegisterBody(String username, String password, String email) {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("email", email);
        return body;
    }

    /**
     * 创建登录请求体
     */
    private Map<String, String> createLoginBody(String username, String password) {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        return body;
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void testRegister_ShouldSucceed() throws Exception {
        Map<String, String> body = createRegisterBody(TEST_USER, TEST_PASSWORD, TEST_EMAIL);

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("注册成功"));
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void testRegister_DuplicateUsername_ShouldFail() throws Exception {
        // 先创建用户
        userRepository.save(new User(TEST_USER, TEST_PASSWORD, TEST_EMAIL));

        // 重复注册
        Map<String, String> body = createRegisterBody(TEST_USER, TEST_PASSWORD, TEST_EMAIL);

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("该用户名已存在"));
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testRegister_InvalidEmail_ShouldFail() throws Exception {
        Map<String, String> body = createRegisterBody(TEST_USER + "_invalid", TEST_PASSWORD, "invalid-email");

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("邮箱格式不正确"));
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void testRegister_EmptyPassword_ShouldFail() throws Exception {
        Map<String, String> body = createRegisterBody(TEST_USER + "_nopass", "", TEST_EMAIL);

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void testLogin_ShouldSucceed() throws Exception {
        // 先创建用户
        userRepository.save(new User(TEST_USER + "_login", TEST_PASSWORD, TEST_EMAIL));

        Map<String, String> body = createLoginBody(TEST_USER + "_login", TEST_PASSWORD);

        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.role").value("customer"));

        // 清理
        userRepository.findByUsername(TEST_USER + "_login").ifPresent(u -> userRepository.delete(u));
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void testLogin_EmptyUsername_ShouldFail() throws Exception {
        Map<String, String> body = createLoginBody("", TEST_PASSWORD);

        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("请输入用户名"));
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    void testLogin_EmptyPassword_ShouldFail() throws Exception {
        Map<String, String> body = createLoginBody(TEST_USER, "");

        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("请输入密码"));
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void testLogin_WrongPassword_ShouldFail() throws Exception {
        // 先创建用户
        userRepository.save(new User(TEST_USER + "_wp", TEST_PASSWORD, TEST_EMAIL));

        Map<String, String> body = createLoginBody(TEST_USER + "_wp", "wrongpassword");

        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("密码错误"));

        // 清理
        userRepository.findByUsername(TEST_USER + "_wp").ifPresent(u -> userRepository.delete(u));
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    void testLogin_DisabledUser_ShouldFail() throws Exception {
        // 创建被禁用的用户
        User disabledUser = new User(TEST_USER + "_disabled", TEST_PASSWORD, TEST_EMAIL);
        disabledUser.setEnabled(false);
        userRepository.save(disabledUser);

        Map<String, String> body = createLoginBody(TEST_USER + "_disabled", TEST_PASSWORD);

        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("您的账号已经被禁用"));

        // 清理
        userRepository.findByUsername(TEST_USER + "_disabled").ifPresent(u -> userRepository.delete(u));
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    void testGetUserByUsername_ShouldSucceed() throws Exception {
        userRepository.save(new User(TEST_USER + "_get", TEST_PASSWORD, TEST_EMAIL));

        mockMvc.perform(get("/api/v1/users/username/" + TEST_USER + "_get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TEST_USER + "_get"))
                .andExpect(jsonPath("$.role").value("customer"));

        userRepository.findByUsername(TEST_USER + "_get").ifPresent(u -> userRepository.delete(u));
    }

    @Test
    @org.junit.jupiter.api.Order(11)
    void testToggleUserEnabled_ShouldToggle() throws Exception {
        User user = userRepository.save(new User(TEST_USER + "_toggle", TEST_PASSWORD, TEST_EMAIL));

        // 禁用
        mockMvc.perform(put("/api/v1/users/" + user.getId() + "/toggle-enabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.enabled").value(false));

        // 解禁
        mockMvc.perform(put("/api/v1/users/" + user.getId() + "/toggle-enabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.enabled").value(true));

        userRepository.delete(user);
    }

    @Test
    @org.junit.jupiter.api.Order(12)
    void testGetAllUsers_ShouldReturnList() throws Exception {
        mockMvc.perform(get("/api/v1/users/all"))
                .andExpect(status().isOk());
    }
}
