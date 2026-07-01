package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.NguoiDungResponse;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.service.NguoiDungService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nguoi-dung")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NguoiDungResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(nguoiDungService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NguoiDungResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(nguoiDungService.getById(id)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<NguoiDungResponse>> register(@Valid @RequestBody RegisterRequest request) {
        NguoiDungResponse created = nguoiDungService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Đăng ký thành công", created));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<NguoiDungResponse>> login(@Valid @RequestBody LoginRequest request) {
        NguoiDungResponse user = nguoiDungService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Đăng nhập thành công", user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NguoiDungResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody RegisterRequest request) {
        NguoiDungResponse updated = nguoiDungService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật người dùng thành công", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        nguoiDungService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa người dùng thành công", null));
    }

    @PostMapping("/reset-password/{id}")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@PathVariable Integer id) {
        nguoiDungService.resetPassword(id, "123456");
        return ResponseEntity.ok(ApiResponse.ok("Reset mật khẩu thành công về 123456", null));
    }

    @PostMapping("/reset-all-passwords")
    public ResponseEntity<ApiResponse<String>> resetAllPasswords() {
        int count = nguoiDungService.resetAllPasswords("123456");
        return ResponseEntity.ok(ApiResponse.ok("Đã reset mật khẩu " + count + " tài khoản về 123456", "OK"));
    }
}
