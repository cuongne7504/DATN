package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.entity.ThuongHieu;
import com.example.backend.repository.ThuongHieuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thuong-hieu")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ThuongHieuController {

    private final ThuongHieuRepository thuongHieuRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ThuongHieu>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(thuongHieuRepository.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ThuongHieu>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(thuongHieuRepository.findById(id).orElse(null)));
    }
}
