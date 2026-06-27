package com.example.backend.controller;

import com.example.backend.entity.DanhMuc;
import com.example.backend.service.DanhMucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/danh-muc")
@CrossOrigin(origins = "*")
public class DanhMucController {

    @Autowired
    private DanhMucService danhMucService;

    @GetMapping
    public ResponseEntity<List<DanhMuc>> getAll() {
        return ResponseEntity.ok(danhMucService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DanhMuc> getById(@PathVariable Integer id) {
        Optional<DanhMuc> danhMuc = danhMucService.getById(id);
        return danhMuc.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DanhMuc> create(@RequestBody DanhMuc danhMuc) {
        return ResponseEntity.ok(danhMucService.save(danhMuc));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DanhMuc> update(@PathVariable Integer id, @RequestBody DanhMuc danhMuc) {
        if (!danhMucService.getById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        danhMuc.setMaDanhMuc(id);
        return ResponseEntity.ok(danhMucService.save(danhMuc));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!danhMucService.getById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        danhMucService.delete(id);
        return ResponseEntity.ok().build();
    }
}

