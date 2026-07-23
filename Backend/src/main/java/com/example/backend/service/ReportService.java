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
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
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

    // TODO: Cần tối ưu lại đoạn query này vì đang dùng vòng lặp lồng nhau, 
    // nếu dữ liệu lớn sẽ bị chậm. Có thể dùng Join query ở Repository để tốt hơn.
    public List<ProfitReportItemDto> calculateProfitReport() {
        List<DonHang> allOrders = donHangRepository.findAll();
        List<ProfitReportItemDto> reportItems = new ArrayList<>();

        for (DonHang donHang : allOrders) {
            List<ChiTietDonHang> chiTietDonHangs = chiTietDonHangRepository.findByMaDonHang(donHang.getMaDonHang());
            for (ChiTietDonHang ctdh : chiTietDonHangs) {
                ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(ctdh.getMaChiTietSp()).orElse(null);
                if (ctsp == null) continue;
                
                SanPham sanPham = sanPhamRepository.findById(ctsp.getMaSanPham()).orElse(null);
                if (sanPham == null) continue;

                BigDecimal donGiaBan = ctdh.getDonGia();
                BigDecimal giaGoc = sanPham.getGiaGoc() != null ? sanPham.getGiaGoc() : BigDecimal.ZERO;
                // Giả định giá nhập = 70% giá gốc để tính lợi nhuận (vì DB không có trường giá nhập)
                BigDecimal giaNhap = giaGoc.multiply(BigDecimal.valueOf(0.7));
                BigDecimal loiNhuanTrenMotSp = donGiaBan.subtract(giaNhap);
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

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        applyThinBorders(headerStyle);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle dataStyle = workbook.createCellStyle();
        applyThinBorders(dataStyle);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle numberStyle = workbook.createCellStyle();
        applyThinBorders(numberStyle);
        numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));

        String[] headers = {"Mã Đơn Hàng", "Ngày Đặt", "Tên Sản Phẩm", "Kích Cỡ", "Màu Sắc", "Số Lượng", "Đơn Giá Bán", "Giá Gốc", "Lợi Nhuận/SP", "Tổng Lợi Nhuận Item"};
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(22);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (ProfitReportItemDto item : reportItems) {
            Row row = sheet.createRow(rowNum++);
            setCellValue(row, 0, item.getMaDonHang(), dataStyle);
            setCellValue(row, 1, item.getNgayDat() != null ? item.getNgayDat().toString() : "", dataStyle);
            setCellValue(row, 2, item.getTenSanPham(), dataStyle);
            setCellValue(row, 3, item.getKichCo(), dataStyle);
            setCellValue(row, 4, item.getMauSac(), dataStyle);
            setCellValue(row, 5, item.getSoLuong(), numberStyle);
            setCellValue(row, 6, item.getDonGiaBan() != null ? item.getDonGiaBan().doubleValue() : 0, numberStyle);
            setCellValue(row, 7, item.getGiaGoc() != null ? item.getGiaGoc().doubleValue() : 0, numberStyle);
            setCellValue(row, 8, item.getLoiNhuanTrenMotSp() != null ? item.getLoiNhuanTrenMotSp().doubleValue() : 0, numberStyle);
            setCellValue(row, 9, item.getTongLoiNhuanItem() != null ? item.getTongLoiNhuanItem().doubleValue() : 0, numberStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            int width = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, Math.min(width + 512, 256 * 40));
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    private void applyThinBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private void setCellValue(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private void setCellValue(Row row, int column, int value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void setCellValue(Row row, int column, double value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
