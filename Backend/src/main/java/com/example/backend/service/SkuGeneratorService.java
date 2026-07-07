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
     * Thuật toán sinh mã vạch ngẫu nhiên 12 số chuẩn mực siêu thị
     * Đảm bảo tính duy nhất 100% trong toàn hệ thống
     */
    public String generateUniqueSku() {
        Random random = new Random();
        String newSku;
        boolean exists;

        do {
            // Sinh ngẫu nhiên chuỗi 12 số
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 12; i++) {
                sb.append(random.nextInt(10));
            }
            newSku = sb.toString();

            // Kiểm tra xem database đã có mã này chưa
            exists = chiTietSanPhamRepository.existsByMaVachSku(newSku);
        } while (exists); // Nếu trùng thì vòng lặp chạy lại sinh mã mới

        return newSku;
    }
}
