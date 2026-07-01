package com.example.backend.service;

import com.example.backend.dto.KhachHangRequest;
import com.example.backend.dto.KhachHangResponse;
import com.example.backend.entity.KhachHang;
import com.example.backend.entity.NguoiDung;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.KhachHangRepository;
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
public class KhachHangService {

    private final KhachHangRepository khachHangRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    public List<KhachHangResponse> getAll() {
        return khachHangRepository.findAllWithNguoiDung()
                .stream()
                .map(KhachHangResponse::new)
                .collect(Collectors.toList());
    }

    public List<KhachHangResponse> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAll();
        }
        return khachHangRepository.searchByKeywordWithNguoiDung(keyword.trim())
                .stream()
                .map(KhachHangResponse::new)
                .collect(Collectors.toList());
    }

    public KhachHangResponse getById(Integer id) {
        KhachHang kh = khachHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng có mã: " + id));
        return new KhachHangResponse(kh);
    }

    @Transactional
    public KhachHangResponse create(KhachHangRequest request) {
        String customerName = resolveCustomerName(request);
        String email = normalizeRequired(request.getEmail(), "Email không được để trống");

        if (nguoiDungRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException("Email đã được sử dụng: " + email);
        }

        NguoiDung user = new NguoiDung();
        user.setMaNguoiDung(nextNguoiDungId());
        user.setMaQuyen(3);
        user.setHoTen(customerName);
        user.setEmail(email);

        String rawPassword = request.getMatKhau();
        if (rawPassword == null || rawPassword.isBlank()) {
            rawPassword = "123456";
        }
        user.setMatKhau(passwordEncoder.encode(rawPassword));
        user.setSoDienThoai(trimToNull(request.getSoDienThoai()));
        user.setDiaChi(trimToNull(request.getDiaChi()));
        user.setNgayTao(LocalDateTime.now());

        NguoiDung savedUser = nguoiDungRepository.save(user);

        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(nextKhachHangId());
        kh.setNguoiDung(savedUser);
        kh.setTenKhachHang(customerName);
        kh.setGioiTinh(trimToNull(request.getGioiTinh()));
        kh.setNgaySinh(request.getNgaySinh());
        kh.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : true);

        return new KhachHangResponse(khachHangRepository.save(kh));
    }

    @Transactional
    public KhachHangResponse update(Integer id, KhachHangRequest request) {
        KhachHang kh = khachHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng có mã: " + id));

        NguoiDung user = kh.getNguoiDung();
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy thông tin tài khoản của khách hàng này");
        }

        String customerName = resolveCustomerName(request);
        String email = normalizeRequired(request.getEmail(), "Email không được để trống");

        nguoiDungRepository.findByEmail(email).ifPresent(existing -> {
            if (!existing.getMaNguoiDung().equals(user.getMaNguoiDung())) {
                throw new BadRequestException("Email đã được sử dụng bởi tài khoản khác");
            }
        });

        user.setHoTen(customerName);
        user.setEmail(email);
        user.setSoDienThoai(trimToNull(request.getSoDienThoai()));
        user.setDiaChi(trimToNull(request.getDiaChi()));

        if (request.getMatKhau() != null && !request.getMatKhau().isBlank()) {
            user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        }

        kh.setTenKhachHang(customerName);
        kh.setGioiTinh(trimToNull(request.getGioiTinh()));
        kh.setNgaySinh(request.getNgaySinh());
        kh.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : true);

        nguoiDungRepository.save(user);
        return new KhachHangResponse(khachHangRepository.save(kh));
    }
    @Transactional
    public KhachHangResponse changeStatus(Integer id, Boolean trangThai) {
        KhachHang kh = khachHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng có mã: " + id));
        kh.setTrangThai(trangThai != null ? trangThai : false);
        return new KhachHangResponse(khachHangRepository.save(kh));
    }

    @Transactional
    public void delete(Integer id) {
        changeStatus(id, false);
    }

    private String resolveCustomerName(KhachHangRequest request) {
        String name = request.getTenKhachHang();
        if (name == null || name.isBlank()) {
            name = request.getHoTen();
        }
        return normalizeRequired(name, "Họ tên không được để trống");
    }

    private String normalizeRequired(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(message);
        }
        return value.trim();
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private Integer nextNguoiDungId() {
        return nguoiDungRepository.findMaxId() + 1;
    }

    private Integer nextKhachHangId() {
        return khachHangRepository.findMaxId() + 1;
    }
}
