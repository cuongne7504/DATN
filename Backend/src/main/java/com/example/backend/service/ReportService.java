package com.example.backend.service;

import com.example.backend.dto.ProfitReportItemDto;
import com.example.backend.entity.ChiTietDonHang;
import com.example.backend.entity.ChiTietSanPham;
import com.example.backend.entity.DonHang;
import com.example.backend.entity.SanPham;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ChiTietDonHangRepository;
import com.example.backend.repository.ChiTietSanPhamRepository;
import com.example.backend.repository.DonHangRepository;
import com.example.backend.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final SanPhamRepository sanPhamRepository;

    public List<ProfitReportItemDto> calculateProfitReport() {
        List<ProfitReportItemDto> reportItems = new ArrayList<>();
        List<DonHang> allOrders = donHangRepository.findAll();

        for (DonHang donHang : allOrders) {
            List<ChiTietDonHang> chiTietDonHangs = chiTietDonHangRepository.findByMaDonHang(donHang.getMaDonHang());
            for (ChiTietDonHang ctdh : chiTietDonHangs) {
                ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(ctdh.getMaChiTietSp())
                        .orElseThrow(() -> new ResourceNotFoundException("Chi tiết sản phẩm không tìm thấy"));
                SanPham sanPham = sanPhamRepository.findById(ctsp.getMaSanPham())
                        .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tìm thấy"));

                BigDecimal donGiaBan = ctdh.getDonGia();
                BigDecimal giaGoc = sanPham.getGiaGoc();
                BigDecimal loiNhuanTrenMotSp = donGiaBan.subtract(giaGoc);
                BigDecimal tongLoiNhuanItem = loiNhuanTrenMotSp.multiply(BigDecimal.valueOf(ctdh.getSoLuong()));

                reportItems.add(new ProfitReportItemDto(
                        donHang.getMaDonHang(),
                        donHang.getNgayDat(),
                        sanPham.getTenSanPham(),
                        ctsp.getKichCo(),
                        ctsp.getMauSac(),
                        ctdh.getSoLuong(),
                        donGiaBan,
                        giaGoc,
                        loiNhuanTrenMotSp,
                        tongLoiNhuanItem
                ));
            }
        }
        return reportItems;
    }

    public byte[] exportProfitReportToExcel(List<ProfitReportItemDto> reportItems) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo lợi nhuận");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Mã Đơn Hàng", "Ngày Đặt", "Tên Sản Phẩm", "Kích Cỡ", "Màu Sắc", "Số Lượng", "Đơn Giá Bán", "Giá Gốc", "Lợi Nhuận/SP", "Tổng Lợi Nhuận Item"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Populate data rows
        int rowNum = 1;
        for (ProfitReportItemDto item : reportItems) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getMaDonHang());
            row.createCell(1).setCellValue(item.getNgayDat().toString());
            row.createCell(2).setCellValue(item.getTenSanPham());
            row.createCell(3).setCellValue(item.getKichCo());
            row.createCell(4).setCellValue(item.getMauSac());
            row.createCell(5).setCellValue(item.getSoLuong());
            row.createCell(6).setCellValue(item.getDonGiaBan().doubleValue());
            row.createCell(7).setCellValue(item.getGiaGoc().doubleValue());
            row.createCell(8).setCellValue(item.getLoiNhuanTrenMotSp().doubleValue());
            row.createCell(9).setCellValue(item.getTongLoiNhuanItem().doubleValue());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }
}
