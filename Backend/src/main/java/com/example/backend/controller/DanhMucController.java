package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.entity.DanhMuc;
import com.example.backend.repository.DanhMucRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/danh-muc")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DanhMucController {

    private final DanhMucRepository danhMucRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DanhMuc>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(danhMucRepository.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DanhMuc>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(danhMucRepository.findById(id).orElse(null)));
    }
}
