package com.example.backend.controller;

import com.example.backend.entity.ThuongHieu;
import com.example.backend.service.ThuongHieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/thuong-hieu")
@CrossOrigin(origins = "*")
public class ThuongHieuController {

    @Autowired
    private ThuongHieuService thuongHieuService;

    @GetMapping
    public ResponseEntity<List<ThuongHieu>> getAll() {
        return ResponseEntity.ok(thuongHieuService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThuongHieu> getById(@PathVariable Integer id) {
        Optional<ThuongHieu> thuongHieu = thuongHieuService.getById(id);
        return thuongHieu.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ThuongHieu> create(@RequestBody ThuongHieu thuongHieu) {
        return ResponseEntity.ok(thuongHieuService.save(thuongHieu));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThuongHieu> update(@PathVariable Integer id, @RequestBody ThuongHieu thuongHieu) {
        if (!thuongHieuService.getById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        thuongHieu.setMaThuongHieu(id);
        return ResponseEntity.ok(thuongHieuService.save(thuongHieu));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!thuongHieuService.getById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        thuongHieuService.delete(id);
        return ResponseEntity.ok().build();
    }
}

