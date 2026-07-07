-- Dòng này đảm bảo bạn đang tạo bảng vào đúng Database SportPro
create database SportPro

USE SportPro;
GO

CREATE TABLE PHAN_QUYEN (
  ma_quyen INT IDENTITY(1,1) PRIMARY KEY,
  ten_quyen NVARCHAR(255),
  mo_ta NVARCHAR(MAX)
);

CREATE TABLE NGUOI_DUNG (
  ma_nguoi_dung INT IDENTITY(1,1) PRIMARY KEY,
  ma_quyen INT,
  ho_ten NVARCHAR(255),
  email VARCHAR(255),
  mat_khau VARCHAR(255),
  so_dien_thoai VARCHAR(20),
  dia_chi NVARCHAR(MAX),
  ngay_tao DATETIME,
  FOREIGN KEY (ma_quyen) REFERENCES PHAN_QUYEN(ma_quyen)
);

CREATE TABLE DANH_MUC (
  ma_danh_muc INT IDENTITY(1,1) PRIMARY KEY,
  ma_danh_muc_cha INT,
  ten_danh_muc NVARCHAR(255),
  hinh_anh VARCHAR(255),
  FOREIGN KEY (ma_danh_muc_cha) REFERENCES DANH_MUC(ma_danh_muc)
);

CREATE TABLE THUONG_HIEU (
  ma_thuong_hieu INT IDENTITY(1,1) PRIMARY KEY,
  ten_thuong_hieu NVARCHAR(255),
  logo VARCHAR(255)
);

CREATE TABLE SAN_PHAM (
  ma_san_pham INT IDENTITY(1,1) PRIMARY KEY,
  ma_danh_muc INT,
  ma_thuong_hieu INT,
  ten_san_pham NVARCHAR(255),
  mo_ta NVARCHAR(MAX),
  gia_goc DECIMAL(10,2),
  gia_khuyen_mai DECIMAL(10,2),
  ngay_tao DATETIME,
  FOREIGN KEY (ma_danh_muc) REFERENCES DANH_MUC(ma_danh_muc),
  FOREIGN KEY (ma_thuong_hieu) REFERENCES THUONG_HIEU(ma_thuong_hieu)
);

CREATE TABLE CHI_TIET_SAN_PHAM (
  ma_chi_tiet_sp INT IDENTITY(1,1) PRIMARY KEY,
  ma_san_pham INT,
  ma_vach_sku VARCHAR(100),
  mau_sac NVARCHAR(50),
  kich_co VARCHAR(20),
  so_luong_ton INT,
  gia_cong_them DECIMAL(10,2),
  FOREIGN KEY (ma_san_pham) REFERENCES SAN_PHAM(ma_san_pham)
);

CREATE TABLE HINH_ANH_SP (
  ma_hinh_anh INT IDENTITY(1,1) PRIMARY KEY,
  ma_san_pham INT,
  duong_dan_anh VARCHAR(255),
  la_anh_chinh BIT,
  FOREIGN KEY (ma_san_pham) REFERENCES SAN_PHAM(ma_san_pham)
);

CREATE TABLE KHUYEN_MAI (
  ma_khuyen_mai INT IDENTITY(1,1) PRIMARY KEY,
  ma_code VARCHAR(50),
  phan_tram_giam INT,
  so_tien_giam DECIMAL(10,2),
  don_toi_thieu DECIMAL(10,2),
  ngay_bat_dau DATETIME,
  ngay_ket_thuc DATETIME,
  so_luong_dung INT
);

CREATE TABLE DON_HANG (
  ma_don_hang INT IDENTITY(1,1) PRIMARY KEY,
  ma_nguoi_dung INT,
  ma_nhan_vien INT,
  ma_khuyen_mai INT,
  ngay_dat DATETIME,
  tong_tien DECIMAL(10,2),
  phi_ship DECIMAL(10,2),
  dia_chi_giao NVARCHAR(MAX),
  phuong_thuc_tt NVARCHAR(50),
  trang_thai NVARCHAR(50),
  FOREIGN KEY (ma_nguoi_dung) REFERENCES NGUOI_DUNG(ma_nguoi_dung),
  FOREIGN KEY (ma_nhan_vien) REFERENCES NGUOI_DUNG(ma_nguoi_dung),
  FOREIGN KEY (ma_khuyen_mai) REFERENCES KHUYEN_MAI(ma_khuyen_mai)
);

CREATE TABLE CHI_TIET_DON_HANG (
  ma_ct_don_hang INT IDENTITY(1,1) PRIMARY KEY,
  ma_don_hang INT,
  ma_chi_tiet_sp INT,
  so_luong INT,
  don_gia DECIMAL(10,2),
  FOREIGN KEY (ma_don_hang) REFERENCES DON_HANG(ma_don_hang),
  FOREIGN KEY (ma_chi_tiet_sp) REFERENCES CHI_TIET_SAN_PHAM(ma_chi_tiet_sp)
);

CREATE TABLE GIO_HANG (
  ma_gio_hang INT IDENTITY(1,1) PRIMARY KEY,
  ma_nguoi_dung INT,
  ngay_tao DATETIME,
  FOREIGN KEY (ma_nguoi_dung) REFERENCES NGUOI_DUNG(ma_nguoi_dung)
);

CREATE TABLE CHI_TIET_GIO_HANG (
  ma_ct_gio_hang INT IDENTITY(1,1) PRIMARY KEY,
  ma_gio_hang INT,
  ma_chi_tiet_sp INT,
  so_luong INT,
  FOREIGN KEY (ma_gio_hang) REFERENCES GIO_HANG(ma_gio_hang),
  FOREIGN KEY (ma_chi_tiet_sp) REFERENCES CHI_TIET_SAN_PHAM(ma_chi_tiet_sp)
);

CREATE TABLE LICH_SU_THANH_TOAN (
  ma_thanh_toan INT IDENTITY(1,1) PRIMARY KEY,
  ma_don_hang INT,
  ma_giao_dich VARCHAR(100),
  so_tien DECIMAL(10,2),
  trang_thai NVARCHAR(50),
  ngay_tao DATETIME,
  FOREIGN KEY (ma_don_hang) REFERENCES DON_HANG(ma_don_hang)
);

CREATE TABLE DANH_GIA (
  ma_danh_gia INT IDENTITY(1,1) PRIMARY KEY,
  ma_nguoi_dung INT,
  ma_san_pham INT,
  so_sao INT,
  noi_dung NVARCHAR(MAX),
  ngay_tao DATETIME,
  FOREIGN KEY (ma_nguoi_dung) REFERENCES NGUOI_DUNG(ma_nguoi_dung),
  FOREIGN KEY (ma_san_pham) REFERENCES SAN_PHAM(ma_san_pham)
);

USE SportPro;
GO

-- 1. Thêm Phân Quyền
SET IDENTITY_INSERT PHAN_QUYEN ON;
GO
INSERT INTO PHAN_QUYEN (ma_quyen, ten_quyen, mo_ta) VALUES
(1, N'Quản trị viên', N'Full quyền hệ thống'),
(2, N'Nhân viên', N'Quyền quản lý đơn hàng và sản phẩm'),
(3, N'Khách hàng', N'Quyền mua hàng và đánh giá');
GO
SET IDENTITY_INSERT PHAN_QUYEN OFF;
GO

-- 2. Thêm Người Dùng
-- (Mật khẩu đang giả lập là đã mã hóa hoặc dùng pass mặc định 123456)
SET IDENTITY_INSERT NGUOI_DUNG ON;
GO
INSERT INTO NGUOI_DUNG (ma_nguoi_dung, ma_quyen, ho_ten, email, mat_khau, so_dien_thoai, dia_chi, ngay_tao) VALUES
(1, 1, N'Nguyễn Trùm Cuối', 'admin@sportpro.com', '123456', '0999999999', N'Hà Nội', GETDATE()),
(2, 2, N'Trần Nhân Viên', 'nhanvien1@sportpro.com', '123456', '0888888888', N'Đà Nẵng', GETDATE()),
(3, 3, N'Lê Khách Mua', 'khachhang1@gmail.com', '123456', '0777777777', N'Hồ Chí Minh', GETDATE()),
(4, 3, N'Phạm Khách Sộp', 'khachhang2@gmail.com', '123456', '0666666666', N'Hải Phòng', GETDATE());
GO
SET IDENTITY_INSERT NGUOI_DUNG OFF;
GO

-- 3. Thêm Danh Mục
-- (Danh mục cha không có ma_danh_muc_cha -> gán NULL)
SET IDENTITY_INSERT DANH_MUC ON;
GO
INSERT INTO DANH_MUC (ma_danh_muc, ma_danh_muc_cha, ten_danh_muc, hinh_anh) VALUES
(1, NULL, N'Quần áo thể thao', 'quan-ao.jpg'),
(2, NULL, N'Giày thể thao', 'giay.jpg'),
(3, 1, N'Áo bóng đá', 'ao-bong-da.jpg'),
(4, 2, N'Giày chạy bộ', 'giay-chay-bo.jpg');
GO
SET IDENTITY_INSERT DANH_MUC OFF;
GO

-- 4. Thêm Thương Hiệu
SET IDENTITY_INSERT THUONG_HIEU ON;
GO
INSERT INTO THUONG_HIEU (ma_thuong_hieu, ten_thuong_hieu, logo) VALUES
(1, N'Nike', 'nike-logo.png'),
(2, N'Adidas', 'adidas-logo.png'),
(3, N'Puma', 'puma-logo.png');
GO
SET IDENTITY_INSERT THUONG_HIEU OFF;
GO

-- 5. Thêm Sản Phẩm
SET IDENTITY_INSERT SAN_PHAM ON;
GO
INSERT INTO SAN_PHAM (ma_san_pham, ma_danh_muc, ma_thuong_hieu, ten_san_pham, mo_ta, gia_goc, gia_khuyen_mai, ngay_tao) VALUES
(1, 3, 2, N'Áo thi đấu CLB Manchester United 23/24', N'Áo sân nhà mùa giải mới, vải thoáng mát.', 500000, 450000, GETDATE()),
(2, 4, 1, N'Giày chạy bộ Nike Air Zoom Pegasus 40', N'Giày chạy siêu nhẹ, đệm êm ái.', 2500000, 2100000, GETDATE()),
(3, 3, 1, N'Áo đội tuyển Việt Nam sân nhà', N'Áo cổ vũ đội tuyển quốc gia.', 300000, 300000, GETDATE());
GO
SET IDENTITY_INSERT SAN_PHAM OFF;
GO

-- 6. Thêm Chi Tiết Sản Phẩm (Các Size và Màu)
SET IDENTITY_INSERT CHI_TIET_SAN_PHAM ON;
GO
INSERT INTO CHI_TIET_SAN_PHAM (ma_chi_tiet_sp, ma_san_pham, ma_vach_sku, mau_sac, kich_co, so_luong_ton, gia_cong_them) VALUES
(1, 1, 'MU23-RED-M', N'Đỏ', 'M', 50, 0),
(2, 1, 'MU23-RED-L', N'Đỏ', 'L', 30, 0),
(3, 2, 'NKPEG40-BLK-42', N'Đen', '42', 20, 0),
(4, 2, 'NKPEG40-WHT-43', N'Trắng', '43', 15, 0),
(5, 3, 'VN-RED-FREESIZE', N'Đỏ', 'FreeSize', 100, 0);
GO
SET IDENTITY_INSERT CHI_TIET_SAN_PHAM OFF;
GO

-- 7. Thêm Hình Ảnh Sản Phẩm (BIT: 1 là TRUE/Có, 0 là FALSE/Không)
SET IDENTITY_INSERT HINH_ANH_SP ON;
GO
INSERT INTO HINH_ANH_SP (ma_hinh_anh, ma_san_pham, duong_dan_anh, la_anh_chinh) VALUES
(1, 1, 'mu_front.jpg', 1),
(2, 1, 'mu_back.jpg', 0),
(3, 2, 'nike_pegasus_main.jpg', 1),
(4, 3, 'vn_ao.jpg', 1);
GO
SET IDENTITY_INSERT HINH_ANH_SP OFF;
GO

-- 8. Thêm Khuyến Mãi
SET IDENTITY_INSERT KHUYEN_MAI ON;
GO
INSERT INTO KHUYEN_MAI (ma_khuyen_mai, ma_code, phan_tram_giam, so_tien_giam, don_toi_thieu, ngay_bat_dau, ngay_ket_thuc, so_luong_dung) VALUES
(1, 'WELCOME50', 10, 50000, 200000, '2024-01-01', '2024-12-31', 1000),
(2, 'FREESHIP', 0, 30000, 150000, '2024-05-01', '2024-06-01', 500);
GO
SET IDENTITY_INSERT KHUYEN_MAI OFF;
GO

-- 9. Thêm Đơn Hàng (Khách 3 mua, Nhân viên 2 duyệt, Áp mã KM 1)
SET IDENTITY_INSERT DON_HANG ON;
GO
INSERT INTO DON_HANG (ma_don_hang, ma_nguoi_dung, ma_nhan_vien, ma_khuyen_mai, ngay_dat, tong_tien, phi_ship, dia_chi_giao, phuong_thuc_tt, trang_thai) VALUES
(1, 3, 2, 1, GETDATE(), 450000, 30000, N'123 Lê Lợi, Q1, TPHCM', N'Chuyển khoản', N'Đã giao hàng'),
(2, 4, 2, NULL, GETDATE(), 2100000, 0, N'456 Lạch Tray, Hải Phòng', N'Tiền mặt (COD)', N'Đang xử lý');
GO
SET IDENTITY_INSERT DON_HANG OFF;
GO

-- 10. Thêm Chi Tiết Đơn Hàng
SET IDENTITY_INSERT CHI_TIET_DON_HANG ON;
GO
INSERT INTO CHI_TIET_DON_HANG (ma_ct_don_hang, ma_don_hang, ma_chi_tiet_sp, so_luong, don_gia) VALUES
(1, 1, 1, 1, 450000), -- Khách 3 mua 1 áo MU size M
(2, 2, 3, 1, 2100000); -- Khách 4 mua 1 đôi Nike size 42
GO
SET IDENTITY_INSERT CHI_TIET_DON_HANG OFF;
GO

-- 11. Thêm Giỏ Hàng (Khách 3 đang để dành đồ)
SET IDENTITY_INSERT GIO_HANG ON;
GO
INSERT INTO GIO_HANG (ma_gio_hang, ma_nguoi_dung, ngay_tao) VALUES
(1, 3, GETDATE());
GO
SET IDENTITY_INSERT GIO_HANG OFF;
GO

-- 12. Thêm Chi Tiết Giỏ Hàng
SET IDENTITY_INSERT CHI_TIET_GIO_HANG ON;
GO
INSERT INTO CHI_TIET_GIO_HANG (ma_ct_gio_hang, ma_gio_hang, ma_chi_tiet_sp, so_luong) VALUES
(1, 1, 4, 1); -- Khách 3 đang bỏ Giày Nike Trắng vào giỏ nhưng chưa thanh toán
GO
SET IDENTITY_INSERT CHI_TIET_GIO_HANG OFF;
GO

-- 13. Thêm Lịch Sử Thanh Toán
SET IDENTITY_INSERT LICH_SU_THANH_TOAN ON;
GO
INSERT INTO LICH_SU_THANH_TOAN (ma_thanh_toan, ma_don_hang, ma_giao_dich, so_tien, trang_thai, ngay_tao) VALUES
(1, 1, 'VNPT_99887766', 480000, N'Thành công', GETDATE());
GO
SET IDENTITY_INSERT LICH_SU_THANH_TOAN OFF;
GO

-- 14. Thêm Đánh Giá
SET IDENTITY_INSERT DANH_GIA ON;
GO
INSERT INTO DANH_GIA (ma_danh_gia, ma_nguoi_dung, ma_san_pham, so_sao, noi_dung, ngay_tao) VALUES
(1, 3, 1, 5, N'Áo đẹp, vải xịn mặc mát lắm shop ơi. Sẽ ủng hộ thêm!', GETDATE());
GO
SET IDENTITY_INSERT DANH_GIA OFF;
GO
