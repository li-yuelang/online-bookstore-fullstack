package com.onlinelibrary.controller;

import com.onlinelibrary.dto.EntityConverter;
import com.onlinelibrary.dto.UserDTO;
import com.onlinelibrary.entity.User;
import com.onlinelibrary.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 用户控制器
 * 
 * 处理用户注册、登录、信息查询，以及管理员对用户的管理操作。
 * 游客端接口：register（注册）、login（登录）、getUserByUsername（查询）
 * 管理员接口：getAllUsers（获取所有用户）、toggleUserEnabled（禁用/解禁）
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String email = request.get("email");

        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(errorResponse("用户名不能为空"));
        }

        if (username.length() < 2 || username.length() > 20) {
            return ResponseEntity.badRequest().body(errorResponse("用户名长度应为2-20个字符"));
        }

        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(errorResponse("密码不能为空"));
        }

        if (password.length() < 6) {
            return ResponseEntity.badRequest().body(errorResponse("密码长度不能少于6位"));
        }

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(errorResponse("邮箱不能为空"));
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return ResponseEntity.badRequest().body(errorResponse("邮箱格式不正确"));
        }

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(errorResponse("该用户名已存在"));
        }

        User user = new User(username, password, email);
        userRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "注册成功");
        result.put("username", username);
        result.put("role", "customer");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        // 前端JS校验提示：如果为空，后端也做校验
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(errorResponse("请输入用户名"));
        }

        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(errorResponse("请输入密码"));
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(errorResponse("该用户名不存在"));
        }

        User user = userOpt.get();

        // 检查用户是否被禁用
        if (!user.getEnabled()) {
            return ResponseEntity.badRequest().body(errorResponse("您的账号已经被禁用"));
        }

        if (!user.getPassword().equals(password)) {
            return ResponseEntity.badRequest().body(errorResponse("密码错误"));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "登录成功");
        result.put("username", username);
        result.put("id", user.getId());
        result.put("role", user.getRole());
        result.put("email", user.getEmail());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            UserDTO dto = EntityConverter.toDTO(userOpt.get());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.status(404).body(errorResponse("用户不存在"));
    }

    // ========== 管理员接口 ==========

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            List<UserDTO> userDTOs = EntityConverter.toUserDTOList(users);
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("获取用户列表失败：" + e.getMessage()));
        }
    }

    @PutMapping("/{id}/toggle-enabled")
    public ResponseEntity<?> toggleUserEnabled(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(404).body(errorResponse("用户不存在"));
            }
            User user = userOpt.get();
            user.setEnabled(!user.getEnabled());
            userRepository.save(user);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", user.getEnabled() ? "用户已解禁" : "用户已被禁用");
            result.put("enabled", user.getEnabled());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorResponse("操作失败：" + e.getMessage()));
        }
    }

    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", message);
        return map;
    }
}
