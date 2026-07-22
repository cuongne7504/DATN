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

    @org.springframework.beans.factory.annotation.Value("${ghn.api.url:https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/create}")
    private String ghnApiUrl;

    @org.springframework.beans.factory.annotation.Value("${ghn.api.token:d6a69466-23cf-11ee-b384-222a7f9d80d6}")
    private String ghnApiToken;

    @org.springframework.beans.factory.annotation.Value("${ghn.shop.id:1234567}")
    private String ghnShopId;

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
        donHang.setMaDonHang(generateNextDonHangId());
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
            ctdh.setMaCtDonHang(generateNextChiTietId());
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
        donHang.setMaDonHang(generateNextDonHangId());
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
            ctdh.setMaCtDonHang(generateNextChiTietId());
            ctdh.setMaDonHang(maDonHang);
            ctdh.setMaChiTietSp(item.getMaChiTietSp());
            ctdh.setSoLuong(item.getSoLuong());
            ctdh.setDonGia(item.getDonGia());
            return chiTietDonHangRepository.save(ctdh);
        }).collect(Collectors.toList());

        return new DonHangDetailResponse(donHang, mapChiTietList(chiTietList), null);
    }

    @Transactional
    public DonHang giaoChoShipper(Integer id, com.example.backend.dto.GiaoHangRequest request) {
        DonHang donHang = donHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng có mã: " + id));
        
        donHang.setShipperName(request.getShipperName());
        donHang.setShipperPhone(request.getShipperPhone());
        donHang.setShippingNote(request.getShippingNote());
        donHang.setTrangThai("Đang giao hàng");
        
        return donHangRepository.save(donHang);
    }

    @Transactional
    public DonHang guiDonSangGHN(Integer id) {
        DonHang donHang = donHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng có mã: " + id));

        // 1. Phân tích địa chỉ giao hàng
        String diaChi = donHang.getDiaChiGiao() != null ? donHang.getDiaChiGiao() : "Khách hàng - 0900000000 - Hà Nội";
        String[] parts = diaChi.split(" - ");
        String toName = "Khách hàng";
        String toPhone = "0900000000";
        String toAddress = diaChi;
        
        if (parts.length >= 3) {
            toName = parts[0].trim();
            toPhone = parts[1].trim();
            StringBuilder addr = new StringBuilder();
            for (int i = 2; i < parts.length; i++) {
                if (i > 2) addr.append(" - ");
                addr.append(parts[i].trim());
            }
            toAddress = addr.toString();
        } else if (parts.length == 2) {
            toName = parts[0].trim();
            toPhone = parts[1].trim();
        }

        // 2. Tạo body gửi đi cho GHN API
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("payment_type_id", 2); // Người nhận trả phí ship
        body.put("note", "Giao hàng từ cửa hàng");
        body.put("required_note", "KHONGCHOXEMHANG");
        body.put("to_name", toName);
        body.put("to_phone", toPhone);
        body.put("to_address", toAddress);
        
        // GHN bắt buộc có to_ward_code và to_district_id. Đây là thông tin mẫu thử nghiệm của GHN (Phường 14, Quận 10, TP.HCM)
        body.put("to_ward_code", "20308");
        body.put("to_district_id", 1442);
        
        // Khối lượng/Kích thước mặc định
        body.put("cod_amount", donHang.getTongTien() != null ? donHang.getTongTien().intValue() : 0);
        body.put("weight", 500); // 500g
        body.put("length", 20); // 20cm
        body.put("width", 15);  // 15cm
        body.put("height", 10); // 10cm
        body.put("service_type_id", 2); // Dịch vụ chuẩn

        // Lấy danh sách sản phẩm thực tế từ đơn hàng đưa vào body (GHN bắt buộc phải có thông tin items)
        List<ChiTietDonHang> chiTietList = chiTietDonHangRepository.findByMaDonHang(id);
        List<java.util.Map<String, Object>> ghnItems = new java.util.ArrayList<>();
        for (ChiTietDonHang ct : chiTietList) {
            ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(ct.getMaChiTietSp()).orElse(null);
            SanPham sp = null;
            if (ctsp != null && ctsp.getMaSanPham() != null) {
                sp = sanPhamRepository.findById(ctsp.getMaSanPham()).orElse(null);
            }
            String itemName = (sp != null) ? sp.getTenSanPham() : "Sản phẩm mã " + ct.getMaChiTietSp();
            java.util.Map<String, Object> ghnItem = new java.util.HashMap<>();
            ghnItem.put("name", itemName);
            ghnItem.put("quantity", ct.getSoLuong());
            ghnItem.put("price", ct.getDonGia() != null ? ct.getDonGia().intValue() : 0);
            ghnItem.put("weight", 200); // mặc định mỗi sp nặng 200g
            ghnItems.add(ghnItem);
        }
        body.put("items", ghnItems);

        // 3. Gửi HTTP Request dùng RestTemplate
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.set("Token", ghnApiToken);
        headers.set("ShopId", ghnShopId);

        org.springframework.http.HttpEntity<java.util.Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(body, headers);

        try {
            org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.postForEntity(ghnApiUrl, entity, java.util.Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                java.util.Map<String, Object> resBody = response.getBody();
                java.util.Map<String, Object> data = (java.util.Map<String, Object>) resBody.get("data");
                if (data != null && data.containsKey("order_code")) {
                    String orderCode = (String) data.get("order_code");
                    
                    // Cập nhật mã vận đơn và trạng thái
                    donHang.setShippingCode(orderCode);
                    donHang.setTrangThai("Đang giao hàng");
                    return donHangRepository.save(donHang);
                } else {
                    throw new BadRequestException("Không lấy được mã vận đơn từ kết quả trả về của GHN.");
                }
            } else {
                throw new BadRequestException("Kết nối tới API GHN thất bại: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new BadRequestException("Lỗi trong quá trình tích hợp GHN: " + e.getMessage());
        }
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
        donHang.setMaDonHang(generateNextDonHangId());
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
            ctdh.setMaCtDonHang(generateNextChiTietId());
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

