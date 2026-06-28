package com.example.backend.service;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.NguoiDungResponse;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.entity.NguoiDung;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    public List<NguoiDungResponse> getAll() {
        return nguoiDungRepository.findAll()
                .stream()
                .map(NguoiDungResponse::new)
                .collect(Collectors.toList());
    }

    public NguoiDungResponse getById(Integer id) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng có mã: " + id));
        return new NguoiDungResponse(nguoiDung);
    }

    @Transactional
    public NguoiDungResponse register(RegisterRequest request) {
        // Kiểm tra email đã tồn tại chưa
        if (nguoiDungRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email đã được sử dụng: " + request.getEmail());
        }

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setMaQuyen(3); // Mặc định là Khách hàng
        nguoiDung.setHoTen(request.getHoTen());
        nguoiDung.setEmail(request.getEmail());
        nguoiDung.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        nguoiDung.setSoDienThoai(request.getSoDienThoai());
        nguoiDung.setDiaChi(request.getDiaChi());
        nguoiDung.setNgayTao(LocalDateTime.now());

        return new NguoiDungResponse(nguoiDungRepository.save(nguoiDung));
    }

    public NguoiDungResponse login(LoginRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Email hoặc mật khẩu không chính xác"));

        if (!passwordEncoder.matches(request.getMatKhau(), nguoiDung.getMatKhau())) {
            throw new BadRequestException("Email hoặc mật khẩu không chính xác");
        }

        return new NguoiDungResponse(nguoiDung);
    }

    @Transactional
    public NguoiDungResponse update(Integer id, RegisterRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng có mã: " + id));

        // Kiểm tra email nếu thay đổi
        nguoiDungRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            if (!existing.getMaNguoiDung().equals(id)) {
                throw new BadRequestException("Email đã được sử dụng bởi tài khoản khác");
            }
        });

        nguoiDung.setHoTen(request.getHoTen());
        nguoiDung.setEmail(request.getEmail());
        if (request.getMatKhau() != null && !request.getMatKhau().isBlank()) {
            nguoiDung.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        }
        nguoiDung.setSoDienThoai(request.getSoDienThoai());
        nguoiDung.setDiaChi(request.getDiaChi());

        return new NguoiDungResponse(nguoiDungRepository.save(nguoiDung));
    }

    @Transactional
    public void delete(Integer id) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng có mã: " + id));
        nguoiDungRepository.delete(nguoiDung);
    }

    @Transactional
    public void resetPassword(Integer id, String newPassword) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng có mã: " + id));
        nguoiDung.setMatKhau(passwordEncoder.encode(newPassword));
        nguoiDungRepository.save(nguoiDung);
    }

    @Transactional
    public int resetAllPasswords(String newPassword) {
        List<NguoiDung> allUsers = nguoiDungRepository.findAll();
        for (NguoiDung user : allUsers) {
            user.setMatKhau(passwordEncoder.encode(newPassword));
        }
        nguoiDungRepository.saveAll(allUsers);
        return allUsers.size();
    }

    private Integer generateNextId() {
        return nguoiDungRepository.findAll().stream()
                .mapToInt(NguoiDung::getMaNguoiDung)
                .max()
                .orElse(0) + 1;
    }
}

