package com.example.backend.service;

import com.example.backend.dto.GioHangDetailResponse;
import com.example.backend.dto.GioHangItemRequest;
import com.example.backend.entity.ChiTietGioHang;
import com.example.backend.entity.ChiTietSanPham;
import com.example.backend.entity.GioHang;
import com.example.backend.entity.SanPham;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ChiTietGioHangRepository;
import com.example.backend.repository.ChiTietSanPhamRepository;
import com.example.backend.repository.GioHangRepository;
import com.example.backend.repository.NguoiDungRepository;
import com.example.backend.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GioHangService {

    private final GioHangRepository gioHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final SanPhamRepository sanPhamRepository;
    private final NguoiDungRepository nguoiDungRepository;

    // Lấy hoặc tạo giỏ hàng mới cho người dùng
    private GioHang getOrCreateCart(Integer maNguoiDung) {
        return gioHangRepository.findByMaNguoiDung(maNguoiDung)
                .orElseGet(() -> {
                    GioHang gio = new GioHang();
                    gio.setMaGioHang(generateNextGioHangId());
                    gio.setMaNguoiDung(maNguoiDung);
                    gio.setNgayTao(LocalDateTime.now());
                    return gioHangRepository.save(gio);
                });
    }

    public GioHangDetailResponse getCartDetail(Integer maNguoiDung) {
        // Kiểm tra user tồn tại
        if (!nguoiDungRepository.existsById(maNguoiDung)) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng có mã: " + maNguoiDung);
        }

        GioHang gioHang = getOrCreateCart(maNguoiDung);
        List<ChiTietGioHang> items = chiTietGioHangRepository.findByMaGioHang(gioHang.getMaGioHang());

        List<GioHangDetailResponse.GioHangItemDetail> itemDetails = items.stream()
                .map(this::buildItemDetail)
                .filter(item -> item != null)
                .collect(Collectors.toList());

        BigDecimal tongTien = itemDetails.stream()
                .map(GioHangDetailResponse.GioHangItemDetail::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new GioHangDetailResponse(gioHang.getMaGioHang(), maNguoiDung, itemDetails, tongTien);
    }

    @Transactional
    public GioHangDetailResponse addItem(Integer maNguoiDung, GioHangItemRequest request) {
        if (!nguoiDungRepository.existsById(maNguoiDung)) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng có mã: " + maNguoiDung);
        }

        ChiTietSanPham ctSp = chiTietSanPhamRepository.findById(request.getMaChiTietSp())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể sản phẩm: " + request.getMaChiTietSp()));

        // Kiểm tra tồn kho
        if (ctSp.getSoLuongTon() < request.getSoLuong()) {
            throw new BadRequestException("Số lượng tồn kho không đủ. Hiện còn: " + ctSp.getSoLuongTon());
        }

        GioHang gioHang = getOrCreateCart(maNguoiDung);

        // Nếu sản phẩm đã có trong giỏ -> cộng thêm số lượng
        Optional<ChiTietGioHang> existing = chiTietGioHangRepository
                .findByMaGioHangAndMaChiTietSp(gioHang.getMaGioHang(), request.getMaChiTietSp());

        if (existing.isPresent()) {
            ChiTietGioHang ctgh = existing.get();
            int newQty = ctgh.getSoLuong() + request.getSoLuong();
            if (ctSp.getSoLuongTon() < newQty) {
                throw new BadRequestException("Số lượng tồn kho không đủ. Hiện còn: " + ctSp.getSoLuongTon());
            }
            ctgh.setSoLuong(newQty);
            chiTietGioHangRepository.save(ctgh);
        } else {
            ChiTietGioHang ctgh = new ChiTietGioHang();
            ctgh.setMaCtGioHang(generateNextCtGioHangId());
            ctgh.setMaGioHang(gioHang.getMaGioHang());
            ctgh.setMaChiTietSp(request.getMaChiTietSp());
            ctgh.setSoLuong(request.getSoLuong());
            chiTietGioHangRepository.save(ctgh);
        }

        return getCartDetail(maNguoiDung);
    }

    @Transactional
    public GioHangDetailResponse updateItem(Integer maNguoiDung, Integer maCtGioHang, Integer soLuong) {
        ChiTietGioHang ctgh = chiTietGioHangRepository.findById(maCtGioHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy item giỏ hàng: " + maCtGioHang));

        if (soLuong <= 0) {
            chiTietGioHangRepository.delete(ctgh);
        } else {
            ChiTietSanPham ctSp = chiTietSanPhamRepository.findById(ctgh.getMaChiTietSp())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể sản phẩm"));
            if (ctSp.getSoLuongTon() < soLuong) {
                throw new BadRequestException("Số lượng tồn kho không đủ. Hiện còn: " + ctSp.getSoLuongTon());
            }
            ctgh.setSoLuong(soLuong);
            chiTietGioHangRepository.save(ctgh);
        }

        return getCartDetail(maNguoiDung);
    }

    @Transactional
    public GioHangDetailResponse removeItem(Integer maNguoiDung, Integer maCtGioHang) {
        ChiTietGioHang ctgh = chiTietGioHangRepository.findById(maCtGioHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy item giỏ hàng: " + maCtGioHang));
        chiTietGioHangRepository.delete(ctgh);
        return getCartDetail(maNguoiDung);
    }

    @Transactional
    public void clearCart(Integer maNguoiDung) {
        gioHangRepository.findByMaNguoiDung(maNguoiDung).ifPresent(gioHang ->
                chiTietGioHangRepository.deleteByMaGioHang(gioHang.getMaGioHang()));
    }

    private GioHangDetailResponse.GioHangItemDetail buildItemDetail(ChiTietGioHang item) {
        return chiTietSanPhamRepository.findById(item.getMaChiTietSp()).map(ctSp -> {
            SanPham sp = sanPhamRepository.findById(ctSp.getMaSanPham()).orElse(null);
            String tenSanPham = sp != null ? sp.getTenSanPham() : "Sản phẩm không tồn tại";

            BigDecimal giaBan = (sp != null ? sp.getGiaKhuyenMai() : BigDecimal.ZERO)
                    .add(ctSp.getGiaCongThem() != null ? ctSp.getGiaCongThem() : BigDecimal.ZERO);

            BigDecimal thanhTien = giaBan.multiply(BigDecimal.valueOf(item.getSoLuong()));

            return new GioHangDetailResponse.GioHangItemDetail(
                    item.getMaCtGioHang(),
                    item.getMaChiTietSp(),
                    ctSp.getMaVachSku(),
                    tenSanPham,
                    ctSp.getMauSac(),
                    ctSp.getKichCo(),
                    item.getSoLuong(),
                    giaBan,
                    thanhTien
            );
        }).orElse(null);
    }

    private Integer generateNextGioHangId() {
        return gioHangRepository.findAll().stream()
                .mapToInt(GioHang::getMaGioHang).max().orElse(0) + 1;
    }

    private Integer generateNextCtGioHangId() {
        return chiTietGioHangRepository.findAll().stream()
                .mapToInt(ChiTietGioHang::getMaCtGioHang).max().orElse(0) + 1;
    }

    // --- CÁC PHƯƠNG THỨC COMPATIBILITY TỪ NHÁNH MAIN ---

    public GioHang getOrCreateGioHang(Integer maNguoiDung) {
        return getOrCreateCart(maNguoiDung);
    }

    public List<ChiTietGioHang> getChiTietGioHang(Integer maNguoiDung) {
        GioHang cart = getOrCreateCart(maNguoiDung);
        return chiTietGioHangRepository.findByMaGioHang(cart.getMaGioHang());
    }

    @Transactional
    public ChiTietGioHang themVaoGio(Integer maNguoiDung, Integer maChiTietSp, Integer soLuong) {
        GioHang cart = getOrCreateCart(maNguoiDung);
        Optional<ChiTietGioHang> existingItem = chiTietGioHangRepository.findByMaGioHangAndMaChiTietSp(cart.getMaGioHang(), maChiTietSp);
        if (existingItem.isPresent()) {
            ChiTietGioHang item = existingItem.get();
            item.setSoLuong(item.getSoLuong() + soLuong);
            return chiTietGioHangRepository.save(item);
        } else {
            ChiTietGioHang newItem = new ChiTietGioHang();
            newItem.setMaCtGioHang(generateNextCtGioHangId());
            newItem.setMaGioHang(cart.getMaGioHang());
            newItem.setMaChiTietSp(maChiTietSp);
            newItem.setSoLuong(soLuong);
            return chiTietGioHangRepository.save(newItem);
        }
    }

    @Transactional
    public ChiTietGioHang capNhatSoLuong(Integer maCtGioHang, Integer soLuongMoi) {
        ChiTietGioHang item = chiTietGioHangRepository.findById(maCtGioHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ"));
        item.setSoLuong(soLuongMoi);
        return chiTietGioHangRepository.save(item);
    }

    @Transactional
    public void xoaKhoiGio(Integer maCtGioHang) {
        chiTietGioHangRepository.deleteById(maCtGioHang);
    }
}
