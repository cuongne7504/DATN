package com.example.backend.service;

import com.example.backend.repository.ChiTietSanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SkuGeneratorService {

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    /**
     * Thuật toán sinh mã SKU ngắn gọn chuẩn format SPPRO0001, SPPRO0002...
     * Đảm bảo ngắn gọn, trực quan và duy nhất 100%
     */
    public String generateUniqueSku() {
        long count = chiTietSanPhamRepository.count();
        String newSku;
        long nextId = count + 1;

        do {
            newSku = String.format("CTSP%02d", nextId++);
        } while (chiTietSanPhamRepository.existsByMaVachSku(newSku));

        return newSku;
    }
}
