package com.example.backend.service;

import com.example.backend.dto.DonHangDetailResponse;
import com.example.backend.dto.TaoDonHangPosRequest;
import com.example.backend.dto.TaoDonHangRequest;
import com.example.backend.entity.ChiTietDonHang;
import com.example.backend.entity.ChiTietSanPham;
import com.example.backend.entity.DonHang;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ChiTietDonHangRepository;
import com.example.backend.repository.ChiTietSanPhamRepository;
import com.example.backend.repository.DonHangRepository;
import com.example.backend.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonHangService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final NguoiDungRepository nguoiDungRepository;

    private List<DonHangDetailResponse.ChiTietDonHangDto> mapChiTietList(List<ChiTietDonHang> chiTietList) {
        return chiTietList.stream().map(ct -> {
            ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(ct.getMaChiTietSp()).orElse(null);
            return new DonHangDetailResponse.ChiTietDonHangDto(ct, ctsp);
        }).collect(Collectors.toList());
    }

    public List<DonHang> getAll() {
        return donHangRepository.findAll();
    }

    public List<DonHang> getByMaNguoiDung(Integer maNguoiDung) {
        if (!nguoiDungRepository.existsById(maNguoiDung)) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng có mã: " + maNguoiDung);
        }
        return donHangRepository.findByMaNguoiDungOrderByNgayDatDesc(maNguoiDung);
    }

    public DonHangDetailResponse getById(Integer id) {
        DonHang donHang = donHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng có mã: " + id));
        List<ChiTietDonHang> chiTietList = chiTietDonHangRepository.findByMaDonHang(id);
        return new DonHangDetailResponse(donHang, mapChiTietList(chiTietList));
    }

    @Transactional
    public DonHangDetailResponse create(TaoDonHangRequest request) {
        if (!nguoiDungRepository.existsById(request.getMaNguoiDung())) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng có mã: " + request.getMaNguoiDung());
        }

        // Kiểm tra tồn kho và trừ tồn kho cho từng sản phẩm
        for (TaoDonHangRequest.DonHangItemRequest item : request.getItems()) {
            ChiTietSanPham ctSp = chiTietSanPhamRepository.findById(item.getMaChiTietSp())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể sản phẩm: " + item.getMaChiTietSp()));

            if (ctSp.getSoLuongTon() < item.getSoLuong()) {
                throw new BadRequestException("Sản phẩm SKU [" + ctSp.getMaVachSku() + "] không đủ tồn kho. Còn: " + ctSp.getSoLuongTon());
            }

            // Trừ tồn kho
            ctSp.setSoLuongTon(ctSp.getSoLuongTon() - item.getSoLuong());
            chiTietSanPhamRepository.save(ctSp);
        }

        // Tính tổng tiền
        BigDecimal tongTien = request.getItems().stream()
                .map(item -> item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Thêm phí ship
        BigDecimal phiShip = request.getPhiShip() != null ? request.getPhiShip() : BigDecimal.ZERO;
        BigDecimal tongCong = tongTien.add(phiShip);

        // Tạo đơn hàng
        DonHang donHang = new DonHang();
        donHang.setMaNguoiDung(request.getMaNguoiDung());
        donHang.setMaKhuyenMai(request.getMaKhuyenMai());
        donHang.setNgayDat(LocalDateTime.now());
        donHang.setTongTien(tongCong);
        donHang.setPhiShip(phiShip);
        donHang.setDiaChiGiao(request.getDiaChiGiao());
        donHang.setPhuongThucTt(request.getPhuongThucTt());
        donHang.setTrangThai("Chờ xử lý");
        donHang = donHangRepository.save(donHang);

        // Lưu chi tiết đơn hàng
        final Integer maDonHang = donHang.getMaDonHang();

        List<ChiTietDonHang> chiTietList = request.getItems().stream().map(item -> {
            ChiTietDonHang ctdh = new ChiTietDonHang();
            ctdh.setMaDonHang(maDonHang);
            ctdh.setMaChiTietSp(item.getMaChiTietSp());
            ctdh.setSoLuong(item.getSoLuong());
            ctdh.setDonGia(item.getDonGia());
            return chiTietDonHangRepository.save(ctdh);
        }).collect(Collectors.toList());

        return new DonHangDetailResponse(donHang, mapChiTietList(chiTietList));
    }

    @Transactional
    public DonHang updateTrangThai(Integer id, String trangThai) {
        DonHang donHang = donHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng có mã: " + id));

        List<String> validStates = List.of("Đang xử lý", "Đang giao hàng", "Đã giao hàng", "Đã hủy");
        if (!validStates.contains(trangThai)) {
            throw new BadRequestException("Trạng thái không hợp lệ: " + trangThai);
        }

        donHang.setTrangThai(trangThai);
        return donHangRepository.save(donHang);
    }

    @Transactional
    public DonHangDetailResponse createPosOrder(TaoDonHangPosRequest request) {
        // Kiểm tra tồn kho và trừ tồn kho cho từng sản phẩm
        for (TaoDonHangPosRequest.DonHangItemRequest item : request.getItems()) {
            ChiTietSanPham ctSp = chiTietSanPhamRepository.findById(item.getMaChiTietSp())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể sản phẩm: " + item.getMaChiTietSp()));

            if (ctSp.getSoLuongTon() < item.getSoLuong()) {
                throw new BadRequestException("Sản phẩm SKU [" + ctSp.getMaVachSku() + "] không đủ tồn kho. Còn: " + ctSp.getSoLuongTon());
            }

            // Trừ tồn kho
            ctSp.setSoLuongTon(ctSp.getSoLuongTon() - item.getSoLuong());
            chiTietSanPhamRepository.save(ctSp);
        }

        // Tính tổng tiền
        BigDecimal tongTien = request.getItems().stream()
                .map(item -> item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tạo đơn hàng POS
        DonHang donHang = new DonHang();
        donHang.setMaNguoiDung(null); // POS order không có khách hàng
        donHang.setMaNhanVien(request.getMaNhanVien());
        donHang.setMaKhuyenMai(null);
        donHang.setNgayDat(LocalDateTime.now());
        donHang.setTongTien(tongTien);
        donHang.setPhiShip(BigDecimal.ZERO); // POS không có phí ship

        String diaChi = "Bán tại quầy";
        if (request.getTenNguoiNhan() != null && !request.getTenNguoiNhan().trim().isEmpty()) {
            diaChi = request.getTenNguoiNhan();
            if (request.getSoDienThoai() != null && !request.getSoDienThoai().trim().isEmpty()) {
                diaChi += " - " + request.getSoDienThoai();
            }
        }
        donHang.setDiaChiGiao(diaChi);

        donHang.setPhuongThucTt("Tiền mặt");
        donHang.setTrangThai("Đã giao hàng"); // POS tự động chuyển sang Đã giao hàng
        donHang = donHangRepository.save(donHang);

        // Lưu chi tiết đơn hàng
        final Integer maDonHang = donHang.getMaDonHang();

        List<ChiTietDonHang> chiTietList = request.getItems().stream().map(item -> {
            ChiTietDonHang ctdh = new ChiTietDonHang();
            ctdh.setMaDonHang(maDonHang);
            ctdh.setMaChiTietSp(item.getMaChiTietSp());
            ctdh.setSoLuong(item.getSoLuong());
            ctdh.setDonGia(item.getDonGia());
            return chiTietDonHangRepository.save(ctdh);
        }).collect(Collectors.toList());

        return new DonHangDetailResponse(donHang, mapChiTietList(chiTietList));
    }

    private Integer generateNextDonHangId() {
        return donHangRepository.findAll().stream()
                .mapToInt(DonHang::getMaDonHang).max().orElse(0) + 1;
    }

    private Integer generateNextChiTietId() {
        return chiTietDonHangRepository.findAll().stream()
                .mapToInt(ChiTietDonHang::getMaCtDonHang).max().orElse(0) + 1;
    }
}

