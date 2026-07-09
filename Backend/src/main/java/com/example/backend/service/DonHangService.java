package com.example.backend.service;

import com.example.backend.dto.DonHangDetailResponse;
import com.example.backend.dto.TaoDonHangPosRequest;
import com.example.backend.dto.TaoDonHangRequest;
import com.example.backend.dto.GuestCheckoutRequest;
import com.example.backend.entity.ChiTietDonHang;
import com.example.backend.entity.ChiTietSanPham;
import com.example.backend.entity.DonHang;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ChiTietDonHangRepository;
import com.example.backend.repository.ChiTietSanPhamRepository;
import com.example.backend.repository.DonHangRepository;
import com.example.backend.repository.KhuyenMaiRepository;
import com.example.backend.repository.NguoiDungRepository;
import com.example.backend.repository.SanPhamRepository;
import com.example.backend.entity.KhuyenMai;
import com.example.backend.entity.SanPham;
import com.example.backend.entity.NguoiDung;
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
    private final KhuyenMaiRepository khuyenMaiRepository;
    private final SanPhamRepository sanPhamRepository;
    private final OtpService otpService;
    private final EmailService emailService;

    private List<DonHangDetailResponse.ChiTietDonHangDto> mapChiTietList(List<ChiTietDonHang> chiTietList) {
        return chiTietList.stream().map(ct -> {
            ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(ct.getMaChiTietSp()).orElse(null);
            SanPham sp = null;
            if (ctsp != null && ctsp.getMaSanPham() != null) {
                sp = sanPhamRepository.findById(ctsp.getMaSanPham()).orElse(null);
            }
            return new DonHangDetailResponse.ChiTietDonHangDto(ct, ctsp, sp);
        }).collect(Collectors.toList());
    }

    public List<DonHang> getAll() {
        return donHangRepository.findAll();
    }

    public List<DonHangDetailResponse> getByMaNguoiDung(Integer maNguoiDung) {
        if (!nguoiDungRepository.existsById(maNguoiDung)) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng có mã: " + maNguoiDung);
        }
        List<DonHang> donHangs = donHangRepository.findByMaNguoiDungOrderByNgayDatDesc(maNguoiDung);
        return donHangs.stream().map(dh -> {
            List<ChiTietDonHang> chiTietList = chiTietDonHangRepository.findByMaDonHang(dh.getMaDonHang());
            KhuyenMai km = dh.getMaKhuyenMai() != null ? khuyenMaiRepository.findById(dh.getMaKhuyenMai()).orElse(null) : null;
            return new DonHangDetailResponse(dh, mapChiTietList(chiTietList), km);
        }).collect(Collectors.toList());
    }

    public DonHangDetailResponse getById(Integer id) {
        DonHang donHang = donHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng có mã: " + id));
        List<ChiTietDonHang> chiTietList = chiTietDonHangRepository.findByMaDonHang(id);
        KhuyenMai km = donHang.getMaKhuyenMai() != null ? khuyenMaiRepository.findById(donHang.getMaKhuyenMai()).orElse(null) : null;
        return new DonHangDetailResponse(donHang, mapChiTietList(chiTietList), km);
    }

    @Transactional
    public DonHangDetailResponse create(TaoDonHangRequest request) {
        NguoiDung user = nguoiDungRepository.findById(request.getMaNguoiDung())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng có mã: " + request.getMaNguoiDung()));

        // Xác thực OTP
        if (request.getOtpCode() == null || request.getOtpCode().trim().isEmpty()) {
            throw new BadRequestException("Vui lòng nhập mã OTP");
        }
        if (!otpService.verifyOtp(user.getSoDienThoai(), request.getOtpCode())) {
            throw new BadRequestException("Mã OTP không chính xác hoặc đã hết hạn");
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

        // Áp dụng khuyến mãi
        if (request.getMaKhuyenMai() != null) {
            KhuyenMai km = khuyenMaiRepository.findById(request.getMaKhuyenMai()).orElse(null);
            if (km != null) {
                if (km.getPhanTramGiam() != null && km.getPhanTramGiam() > 0) {
                    BigDecimal giam = tongTien.multiply(BigDecimal.valueOf(km.getPhanTramGiam())).divide(BigDecimal.valueOf(100));
                    tongCong = tongCong.subtract(giam);
                } else if (km.getSoTienGiam() != null) {
                    tongCong = tongCong.subtract(km.getSoTienGiam());
                }
                
                if (tongCong.compareTo(BigDecimal.ZERO) < 0) {
                    tongCong = BigDecimal.ZERO;
                }
                
                if (km.getSoLuongDung() != null && km.getSoLuongDung() > 0) {
                    km.setSoLuongDung(km.getSoLuongDung() - 1);
                    khuyenMaiRepository.save(km);
                }
            }
        }

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

        KhuyenMai km = donHang.getMaKhuyenMai() != null ? khuyenMaiRepository.findById(donHang.getMaKhuyenMai()).orElse(null) : null;
        
        // Gửi email xác nhận
        emailService.sendOrderReceipt(donHang, user.getEmail());

        return new DonHangDetailResponse(donHang, mapChiTietList(chiTietList), km);
    }

    @Transactional
    public DonHang updateTrangThai(Integer id, String trangThai) {
        DonHang donHang = donHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng có mã: " + id));

        List<String> validStates = List.of("Đang xử lý", "Đang giao hàng", "Đã giao hàng", "Đã hủy");
        if (!validStates.contains(trangThai)) {
            throw new BadRequestException("Trạng thái không hợp lệ: " + trangThai);
        }

        // Nếu chuyển sang Đã hủy và trạng thái cũ khác Đã hủy
        if ("Đã hủy".equals(trangThai) && !"Đã hủy".equals(donHang.getTrangThai())) {
            // Hoàn lại số lượng tồn kho
            List<ChiTietDonHang> chiTietList = chiTietDonHangRepository.findByMaDonHang(id);
            for (ChiTietDonHang ct : chiTietList) {
                ChiTietSanPham ctSp = chiTietSanPhamRepository.findById(ct.getMaChiTietSp()).orElse(null);
                if (ctSp != null) {
                    ctSp.setSoLuongTon(ctSp.getSoLuongTon() + ct.getSoLuong());
                    chiTietSanPhamRepository.save(ctSp);
                }
            }
            
            // Hoàn lại số lượng mã khuyến mãi
            if (donHang.getMaKhuyenMai() != null) {
                KhuyenMai km = khuyenMaiRepository.findById(donHang.getMaKhuyenMai()).orElse(null);
                if (km != null && km.getSoLuongDung() != null) {
                    km.setSoLuongDung(km.getSoLuongDung() + 1);
                    khuyenMaiRepository.save(km);
                }
            }
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

        BigDecimal tongCong = tongTien;

        // Áp dụng khuyến mãi nếu có
        if (request.getMaKhuyenMai() != null) {
            KhuyenMai km = khuyenMaiRepository.findById(request.getMaKhuyenMai()).orElse(null);
            if (km != null) {
                if (km.getPhanTramGiam() != null && km.getPhanTramGiam() > 0) {
                    BigDecimal giam = tongTien.multiply(BigDecimal.valueOf(km.getPhanTramGiam())).divide(BigDecimal.valueOf(100));
                    tongCong = tongCong.subtract(giam);
                } else if (km.getSoTienGiam() != null) {
                    tongCong = tongCong.subtract(km.getSoTienGiam());
                }
                
                if (tongCong.compareTo(BigDecimal.ZERO) < 0) {
                    tongCong = BigDecimal.ZERO;
                }
                
                if (km.getSoLuongDung() != null && km.getSoLuongDung() > 0) {
                    km.setSoLuongDung(km.getSoLuongDung() - 1);
                    khuyenMaiRepository.save(km);
                }
            }
        }

        // Tạo đơn hàng POS
        DonHang donHang = new DonHang();
        donHang.setMaNguoiDung(null); // POS order không có khách hàng
        donHang.setMaNhanVien(request.getMaNhanVien());
        donHang.setMaKhuyenMai(request.getMaKhuyenMai());
        donHang.setNgayDat(LocalDateTime.now());
        donHang.setTongTien(tongCong);
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

        return new DonHangDetailResponse(donHang, mapChiTietList(chiTietList), null);
    }

    @Transactional
    public DonHangDetailResponse createGuestOrder(GuestCheckoutRequest request) {
        // Xác thực OTP
        if (request.getOtpCode() == null || request.getOtpCode().trim().isEmpty()) {
            throw new BadRequestException("Vui lòng nhập mã OTP");
        }
        if (!otpService.verifyOtp(request.getSoDienThoai(), request.getOtpCode())) {
            throw new BadRequestException("Mã OTP không chính xác hoặc đã hết hạn");
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

        // Tìm hoặc tạo NGUOI_DUNG ảo
        com.example.backend.entity.NguoiDung guestUser = nguoiDungRepository.findByEmail(request.getEmail()).orElse(null);
        if (guestUser == null) {
            guestUser = new com.example.backend.entity.NguoiDung();
            guestUser.setEmail(request.getEmail());
            guestUser.setHoTen(request.getHoTen());
            guestUser.setSoDienThoai(request.getSoDienThoai());
            guestUser.setDiaChi(request.getDiaChiGiao());
            guestUser.setMaQuyen(3); // Khách hàng
            // Mật khẩu ngẫu nhiên cho khách vãng lai (họ có thể Quên mật khẩu để lấy lại)
            guestUser.setMatKhau("$2a$10$xyzTemptempTemptempTemptempTemptempTemptempTem"); // Mã hóa tạm
            guestUser.setNgayTao(LocalDateTime.now());
            guestUser = nguoiDungRepository.save(guestUser);
        }

        // Tính tổng tiền
        BigDecimal tongTien = request.getItems().stream()
                .map(item -> item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Thêm phí ship
        BigDecimal phiShip = request.getPhiShip() != null ? request.getPhiShip() : BigDecimal.ZERO;
        BigDecimal tongCong = tongTien.add(phiShip);

        // Áp dụng khuyến mãi
        if (request.getMaKhuyenMai() != null) {
            KhuyenMai km = khuyenMaiRepository.findById(request.getMaKhuyenMai()).orElse(null);
            if (km != null) {
                if (km.getPhanTramGiam() != null && km.getPhanTramGiam() > 0) {
                    BigDecimal giam = tongTien.multiply(BigDecimal.valueOf(km.getPhanTramGiam())).divide(BigDecimal.valueOf(100));
                    tongCong = tongCong.subtract(giam);
                } else if (km.getSoTienGiam() != null) {
                    tongCong = tongCong.subtract(km.getSoTienGiam());
                }
                
                if (tongCong.compareTo(BigDecimal.ZERO) < 0) {
                    tongCong = BigDecimal.ZERO;
                }
                
                if (km.getSoLuongDung() != null && km.getSoLuongDung() > 0) {
                    km.setSoLuongDung(km.getSoLuongDung() - 1);
                    khuyenMaiRepository.save(km);
                }
            }
        }

        // Tạo đơn hàng
        DonHang donHang = new DonHang();
        donHang.setMaNguoiDung(guestUser.getMaNguoiDung());
        donHang.setMaKhuyenMai(request.getMaKhuyenMai());
        donHang.setNgayDat(LocalDateTime.now());
        donHang.setTongTien(tongCong);
        donHang.setPhiShip(phiShip);
        donHang.setDiaChiGiao(request.getDiaChiGiao() + " (" + request.getHoTen() + " - " + request.getSoDienThoai() + ")");
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

        KhuyenMai km = donHang.getMaKhuyenMai() != null ? khuyenMaiRepository.findById(donHang.getMaKhuyenMai()).orElse(null) : null;
        
        // Gửi email xác nhận
        emailService.sendOrderReceipt(donHang, request.getEmail());

        return new DonHangDetailResponse(donHang, mapChiTietList(chiTietList), km);
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

