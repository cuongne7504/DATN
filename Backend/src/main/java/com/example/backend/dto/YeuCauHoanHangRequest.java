package com.example.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class YeuCauHoanHangRequest {
    private Integer maDonHang;
    private String lyDo;
    private String hinhAnhMinhHoa;
    private BigDecimal soTienHoan;
}
