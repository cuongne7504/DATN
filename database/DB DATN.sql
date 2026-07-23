-- Dòng này đảm bảo bạn đang tạo bảng vào đúng Database SportPro
create database SportPro
GO

USE SportPro;
GO

-- ==========================================
-- TẠO CẤU TRÚC BẢNG (DATABASE SCHEMA)
-- ==========================================

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

CREATE TABLE SAN_PHAM_YEU_THICH (
  ma_yeu_thich INT IDENTITY(1,1) PRIMARY KEY,
  ma_nguoi_dung INT,
  ma_san_pham INT,
  ngay_tao DATETIME,
  FOREIGN KEY (ma_nguoi_dung) REFERENCES NGUOI_DUNG(ma_nguoi_dung),
  FOREIGN KEY (ma_san_pham) REFERENCES SAN_PHAM(ma_san_pham)
);

CREATE TABLE CHI_TIET_SAN_PHAM (
  ma_chi_tiet_sp INT IDENTITY(1,1) PRIMARY KEY,
  ma_san_pham INT,
  ma_vach_sku VARCHAR(100),
  mau_sac NVARCHAR(50),
  kich_co VARCHAR(20),
  so_luong_ton INT,
  gia_cong_them DECIMAL(10,2) DEFAULT 0,
  FOREIGN KEY (ma_san_pham) REFERENCES SAN_PHAM(ma_san_pham)
);

CREATE TABLE HINH_ANH_SP (
  ma_hinh_anh INT IDENTITY(1,1) PRIMARY KEY,
  ma_san_pham INT,
  duong_dan_anh NVARCHAR(MAX),  -- Hỗ trợ lưu URL dài hoặc chuỗi base64
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
  shipper_name NVARCHAR(255),
  shipper_phone VARCHAR(20),
  shipping_note NVARCHAR(MAX),
  shipping_code VARCHAR(100),
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

CREATE TABLE YEU_CAU_HOAN_HANG (
  ma_yeu_cau INT IDENTITY(1,1) PRIMARY KEY,
  ma_don_hang INT,
  ly_do NVARCHAR(MAX),
  hinh_anh_minh_hoa VARCHAR(255),
  so_tien_hoan DECIMAL(10,2),
  trang_thai NVARCHAR(50),
  ngay_tao DATETIME,
  ngay_cap_nhat DATETIME,
  FOREIGN KEY (ma_don_hang) REFERENCES DON_HANG(ma_don_hang)
);
GO


-- ==========================================
-- CHÈN DỮ LIỆU MẪU ĐẦY ĐỦ (DỮ LIỆU CHÍNH)
-- ==========================================

-- 1. Thêm Phân Quyền
SET IDENTITY_INSERT PHAN_QUYEN ON;
INSERT INTO PHAN_QUYEN (ma_quyen, ten_quyen, mo_ta) VALUES
(1, N'Quản trị viên', N'Full quyền hệ thống'),
(2, N'Nhân viên', N'Quyền quản lý đơn hàng và sản phẩm'),
(3, N'Khách hàng', N'Quyền mua hàng và đánh giá');
SET IDENTITY_INSERT PHAN_QUYEN OFF;
GO

-- 2. Thêm Tài Khoản Quản Trị Viên (Mật khẩu BCrypt cho "123456")
SET IDENTITY_INSERT NGUOI_DUNG ON;
INSERT INTO NGUOI_DUNG (ma_nguoi_dung, ma_quyen, ho_ten, email, mat_khau, so_dien_thoai, dia_chi, ngay_tao) VALUES
(1, 1, N'Nguyễn Mạnh Cường', 'admin@sportpro.com', '$2a$10$t/do9MfRTY4PtTpd/cP/5.R5b43jwfadEjvLpCn.7iWI4fgRceZgW', '0999999999', N'Hà Nội', GETDATE());
SET IDENTITY_INSERT NGUOI_DUNG OFF;
GO

-- 3. Thêm Danh Mục
INSERT INTO DANH_MUC (ten_danh_muc, hinh_anh) VALUES 
(N'Áo Thể Thao', ''),
(N'Quần Thể Thao', ''),
(N'Giày Thể Thao', ''),
(N'Phụ Kiện', '');

-- 4. Thêm Thương Hiệu
INSERT INTO THUONG_HIEU (ten_thuong_hieu, logo) VALUES 
('Nike', ''),
('Adidas', ''),
('Puma', ''),
('Under Armour', '');

-- 5. Thêm Sản Phẩm mẫu phong phú
INSERT INTO SAN_PHAM (ten_san_pham, mo_ta, gia_goc, gia_khuyen_mai, ma_danh_muc, ma_thuong_hieu, ngay_tao) VALUES 
(N'Áo chạy bộ Nike Dri-FIT', N'Áo thun chạy bộ thoáng khí, thấm hút mồ hôi cực tốt.', 500000, 450000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Áo Thể Thao'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Nike'), GETDATE()),
(N'Quần short thể thao Adidas', N'Quần thể thao cạp chun thoải mái cho mọi hoạt động.', 400000, 350000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Quần Thể Thao'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Adidas'), GETDATE()),
(N'Giày chạy bộ Puma Ultraride', N'Giày siêu nhẹ dành cho chạy bộ chuyên nghiệp.', 2500000, 2000000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Giày Thể Thao'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Puma'), GETDATE()),
(N'Mũ lưỡi trai Under Armour', N'Mũ thể thao chống nắng, form cứng cáp.', 300000, 250000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Phụ Kiện'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Under Armour'), GETDATE());

-- 6. Thêm Hình Ảnh Sản Phẩm mẫu
INSERT INTO HINH_ANH_SP (ma_san_pham, duong_dan_anh, la_anh_chinh) VALUES 
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Áo chạy bộ Nike Dri-FIT'), 'https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Quần short thể thao Adidas'), 'https://images.unsplash.com/photo-1565084888279-aca607ecce0c?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Giày chạy bộ Puma Ultraride'), 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Mũ lưỡi trai Under Armour'), 'https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=600', 1);

-- 7. Thêm Chi Tiết Sản Phẩm (Biến thể Size & Màu)
INSERT INTO CHI_TIET_SAN_PHAM (ma_san_pham, ma_vach_sku, mau_sac, kich_co, so_luong_ton, gia_cong_them) VALUES 
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Áo chạy bộ Nike Dri-FIT'), 'NK-DF-BLK-M', N'Đen', 'M', 50, 0),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Áo chạy bộ Nike Dri-FIT'), 'NK-DF-BLK-L', N'Đen', 'L', 30, 0),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Áo chạy bộ Nike Dri-FIT'), 'NK-DF-WHT-M', N'Trắng', 'M', 20, 0),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Quần short thể thao Adidas'), 'AD-SH-BLK-L', N'Đen', 'L', 40, 0),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Giày chạy bộ Puma Ultraride'), 'PM-UR-RED-42', N'Đỏ', '42', 15, 0),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Giày chạy bộ Puma Ultraride'), 'PM-UR-BLK-43', N'Đen', '43', 10, 0),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Mũ lưỡi trai Under Armour'), 'UA-CP-GRY-FS', N'Xám', 'Freesize', 100, 0);

-- 8. Thêm Nhân viên mẫu (Mật khẩu BCrypt: 123456)
INSERT INTO NGUOI_DUNG (ho_ten, email, so_dien_thoai, dia_chi, mat_khau, ma_quyen, ngay_tao) VALUES 
(N'Nhân Viên 1', 'nhanvien1@sportpro.com', '0988888881', N'Hà Nội', '$2a$10$t/do9MfRTY4PtTpd/cP/5.R5b43jwfadEjvLpCn.7iWI4fgRceZgW', 2, GETDATE()),
(N'Nhân Viên 2', 'nhanvien2@sportpro.com', '0988888882', N'Hồ Chí Minh', '$2a$10$t/do9MfRTY4PtTpd/cP/5.R5b43jwfadEjvLpCn.7iWI4fgRceZgW', 2, GETDATE());

-- 9. Thêm Khách Hàng mẫu (Mật khẩu BCrypt: 123456)
INSERT INTO NGUOI_DUNG (ho_ten, email, so_dien_thoai, dia_chi, mat_khau, ma_quyen, ngay_tao) VALUES 
(N'Khách Hàng 1', 'khachhang@sportpro.com', '0912345678', N'Hà Nội', '$2a$10$t/do9MfRTY4PtTpd/cP/5.R5b43jwfadEjvLpCn.7iWI4fgRceZgW', 3, GETDATE()),
(N'Khách Hàng 2', 'khachhang2@sportpro.com', '0912345679', N'Đà Nẵng', '$2a$10$t/do9MfRTY4PtTpd/cP/5.R5b43jwfadEjvLpCn.7iWI4fgRceZgW', 3, GETDATE());

-- 10. Thêm Khuyến Mãi mẫu
INSERT INTO KHUYEN_MAI (ma_code, phan_tram_giam, so_tien_giam, don_toi_thieu, ngay_bat_dau, ngay_ket_thuc, so_luong_dung) VALUES
('WELCOME50', 10, 50000, 200000, '2024-01-01', '2030-12-31', 1000),
('FREESHIP', 0, 30000, 150000, '2024-05-01', '2030-06-01', 500);

-- 11. Thêm Đơn Hàng Mẫu 
INSERT INTO DON_HANG (ma_nguoi_dung, ma_nhan_vien, trang_thai, tong_tien, phi_ship, dia_chi_giao, phuong_thuc_tt, ngay_dat) VALUES 
((SELECT TOP 1 ma_nguoi_dung FROM NGUOI_DUNG WHERE email = 'khachhang@sportpro.com'), (SELECT TOP 1 ma_nguoi_dung FROM NGUOI_DUNG WHERE email = 'nhanvien1@sportpro.com'), N'Đã giao hàng', 450000, 30000, N'Hà Nội', N'COD', DATEADD(day, -5, GETDATE())),
((SELECT TOP 1 ma_nguoi_dung FROM NGUOI_DUNG WHERE email = 'khachhang@sportpro.com'), (SELECT TOP 1 ma_nguoi_dung FROM NGUOI_DUNG WHERE email = 'nhanvien2@sportpro.com'), N'Đang xử lý', 2000000, 0, N'Hà Nội', N'VNPAY', DATEADD(day, -2, GETDATE()));

-- 12. Thêm Chi Tiết Đơn Hàng
INSERT INTO CHI_TIET_DON_HANG (ma_don_hang, ma_chi_tiet_sp, so_luong, don_gia) VALUES 
(
  (SELECT TOP 1 ma_don_hang FROM DON_HANG WHERE tong_tien = 450000), 
  (SELECT TOP 1 ma_chi_tiet_sp FROM CHI_TIET_SAN_PHAM WHERE ma_san_pham = (SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Áo chạy bộ Nike Dri-FIT')), 
  1, 450000
),
(
  (SELECT TOP 1 ma_don_hang FROM DON_HANG WHERE tong_tien = 2000000), 
  (SELECT TOP 1 ma_chi_tiet_sp FROM CHI_TIET_SAN_PHAM WHERE ma_san_pham = (SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Giày chạy bộ Puma Ultraride')), 
  1, 2000000
);

-- 13. Thêm Lịch Sử Thanh Toán
INSERT INTO LICH_SU_THANH_TOAN (ma_don_hang, ma_giao_dich, so_tien, trang_thai, ngay_tao) VALUES
((SELECT TOP 1 ma_don_hang FROM DON_HANG WHERE tong_tien = 2000000), 'VNPAY_123456', 2000000, N'Thành công', GETDATE());

-- 14. Thêm Đánh Giá
INSERT INTO DANH_GIA (ma_nguoi_dung, ma_san_pham, so_sao, noi_dung, ngay_tao) VALUES
((SELECT TOP 1 ma_nguoi_dung FROM NGUOI_DUNG WHERE email = 'khachhang@sportpro.com'), (SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Áo chạy bộ Nike Dri-FIT'), 5, N'Áo đẹp, vải xịn mặc mát lắm shop ơi. Sẽ ủng hộ thêm!', GETDATE());
-- 15. Thêm Yêu Cầu Hoàn Hàng
INSERT INTO YEU_CAU_HOAN_HANG (ma_don_hang, ly_do, hinh_anh_minh_hoa, so_tien_hoan, trang_thai, ngay_tao, ngay_cap_nhat) VALUES
((SELECT TOP 1 ma_don_hang FROM DON_HANG WHERE tong_tien = 450000), N'Sản phẩm bị lỗi chỉ', 'https://via.placeholder.com/150?text=Loi+Chi', 450000, N'Chờ duyệt', GETDATE(), GETDATE());
GO

-- ==========================================
-- CẬP NHẬT DB ĐÃ TẠO TRƯỚC ĐÓ (chạy 1 lần)
-- Nếu DB SportPro đã tồn tại từ bản script cũ, copy đoạn dưới chạy trong SSMS.
-- Chỉ sửa file .sql KHÔNG tự cập nhật database đang chạy trên máy bạn.
-- ==========================================
USE SportPro;
GO

IF COL_LENGTH('DON_HANG', 'shipper_name') IS NULL
    ALTER TABLE DON_HANG ADD shipper_name NVARCHAR(255) NULL;

IF COL_LENGTH('DON_HANG', 'shipper_phone') IS NULL
    ALTER TABLE DON_HANG ADD shipper_phone VARCHAR(20) NULL;

IF COL_LENGTH('DON_HANG', 'shipping_note') IS NULL
    ALTER TABLE DON_HANG ADD shipping_note NVARCHAR(MAX) NULL;

IF COL_LENGTH('DON_HANG', 'shipping_code') IS NULL
    ALTER TABLE DON_HANG ADD shipping_code VARCHAR(100) NULL;
GO