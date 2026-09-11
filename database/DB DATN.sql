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
  trang_thai NVARCHAR(50) DEFAULT N'Đang kinh doanh',
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
  trang_thai NVARCHAR(50) DEFAULT N'Đang bán',
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
-- CHÈN DỮ LIỆU MẪU CHUẨN QUẦN ÁO ĐÁ BÓNG WORLD CUP
-- ==========================================

DELETE FROM YEU_CAU_HOAN_HANG;
DELETE FROM LICH_SU_THANH_TOAN;
DELETE FROM CHI_TIET_DON_HANG;
DELETE FROM DON_HANG;
DELETE FROM CHI_TIET_GIO_HANG;
DELETE FROM GIO_HANG;
DELETE FROM DANH_GIA;
DELETE FROM SAN_PHAM_YEU_THICH;
DELETE FROM HINH_ANH_SP;
DELETE FROM CHI_TIET_SAN_PHAM;
DELETE FROM SAN_PHAM;
DELETE FROM THUONG_HIEU;
DELETE FROM DANH_MUC;
GO

-- 1. Thêm Phân Quyền (nếu chưa có)
IF NOT EXISTS (SELECT 1 FROM PHAN_QUYEN WHERE ma_quyen = 1)
BEGIN
    SET IDENTITY_INSERT PHAN_QUYEN ON;
    INSERT INTO PHAN_QUYEN (ma_quyen, ten_quyen, mo_ta) VALUES
    (1, N'Quản trị viên', N'Full quyền hệ thống'),
    (2, N'Nhân viên', N'Quyền quản lý đơn hàng và sản phẩm'),
    (3, N'Khách hàng', N'Quyền mua hàng và đánh giá');
    SET IDENTITY_INSERT PHAN_QUYEN OFF;
END
GO

-- 2. Thêm Tài Khoản Quản Trị Viên (Mật khẩu BCrypt cho "123456")
IF NOT EXISTS (SELECT 1 FROM NGUOI_DUNG WHERE ma_nguoi_dung = 1)
BEGIN
    SET IDENTITY_INSERT NGUOI_DUNG ON;
    INSERT INTO NGUOI_DUNG (ma_nguoi_dung, ma_quyen, ho_ten, email, mat_khau, so_dien_thoai, dia_chi, ngay_tao) VALUES
    (1, 1, N'Nguyễn Mạnh Cường', 'admin@sportpro.com', '$2a$10$t/do9MfRTY4PtTpd/cP/5.R5b43jwfadEjvLpCn.7iWI4fgRceZgW', '0999999999', N'Hà Nội', GETDATE());
    SET IDENTITY_INSERT NGUOI_DUNG OFF;
END
GO

-- 3. Thêm Danh Mục Quần Áo Đá Bóng World Cup
INSERT INTO DANH_MUC (ten_danh_muc, hinh_anh) VALUES 
(N'Áo Đấu Đội Tuyển World Cup', 'https://images.unsplash.com/photo-1577223625816-7546f13df25d?w=600'),
(N'Quần Đá Bóng World Cup', 'https://images.unsplash.com/photo-1517466787929-bc90951d0974?w=600'),
(N'Bộ Thi Đấu World Cup', 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=600'),
(N'Giày Đá Bóng World Cup', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600'),
(N'Phụ Kiện Đá Bóng WC', 'https://images.unsplash.com/photo-1614632537197-38a17061c2bd?w=600');

-- 4. Thêm Thương Hiệu Thể Thao
INSERT INTO THUONG_HIEU (ten_thuong_hieu, logo) VALUES 
('Adidas', 'https://upload.wikimedia.org/wikipedia/commons/2/20/Adidas_Logo.svg'),
('Nike', 'https://upload.wikimedia.org/wikipedia/commons/a/a6/Logo_NIKE.svg'),
('Puma', 'https://upload.wikimedia.org/wikipedia/commons/8/88/Puma_Logo.svg'),
('Hummel', ''),
('Mizuno', '');

-- 5. Thêm Sản Phẩm Chuẩn Quần Áo Đá Bóng World Cup
INSERT INTO SAN_PHAM (ten_san_pham, mo_ta, gia_goc, gia_khuyen_mai, ma_danh_muc, ma_thuong_hieu, ngay_tao) VALUES 
(N'Áo Đấu ĐT Argentina World Cup 3 Sao (Messi #10)', N'Áo thi đấu chính thức ĐT Argentina thêu 3 ngôi sao vô địch, chất liệu thun lạnh vi kim cao cấp.', 650000, 650000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Áo Đấu Đội Tuyển World Cup'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Adidas'), GETDATE()),
(N'Áo Đấu ĐT Bồ Đào Nha World Cup (Ronaldo #7)', N'Áo thi đấu ĐT Bồ Đào Nha World Cup đỏ xanh kiêu hãnh, co dãn 4 chiều thể thao chuyên nghiệp.', 620000, 620000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Áo Đấu Đội Tuyển World Cup'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Nike'), GETDATE()),
(N'Áo Đấu ĐT Pháp World Cup (Mbappé #10)', N'Áo thi đấu ĐT Pháp World Cup phối màu Xanh Navy mạ vàng đồng sang trọng, vải Jacquard thoáng khí.', 640000, 640000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Áo Đấu Đội Tuyển World Cup'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Nike'), GETDATE()),
(N'Áo Đấu ĐT Đức World Cup Sân Nhà', N'Áo thi đấu ĐT Đức World Cup dải sọc đen dọc thân áo độc đáo, công nghệ làm mát HEAT.RDY.', 590000, 590000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Áo Đấu Đội Tuyển World Cup'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Adidas'), GETDATE()),
(N'Quần Đá Bóng ĐT Argentina Đen Chuyên Nghiệp', N'Quần đùi thi đấu ĐT Argentina sọc đen xanh, vải dù thể thao siêu nhẹ tích hợp dây rút cạp chun.', 250000, 250000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Quần Đá Bóng World Cup'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Adidas'), GETDATE()),
(N'Quần Short Đá Bóng ĐT Bồ Đào Nha Xanh Đen', N'Quần short bóng đá Bồ Đào Nha form ôm thể thao, chất liệu vải thun co dãn thoáng khí.', 240000, 240000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Quần Đá Bóng World Cup'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Nike'), GETDATE()),
(N'Bộ Quần Áo Đá Bóng Brazil World Cup (Neymar #10)', N'Bộ trang phục trọn bộ Áo + Quần ĐT Brazil World Cup hoa văn da báo cách điệu 5 sao.', 790000, 790000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Bộ Thi Đấu World Cup'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Nike'), GETDATE()),
(N'Bộ Quần Áo Đá Bóng Nhật Bản World Cup (Origami)', N'Bộ thi đấu trọn bộ ĐT Nhật Bản World Cup họa tiết Origami chú chim hạc gấp độc đáo.', 750000, 750000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Bộ Thi Đấu World Cup'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Adidas'), GETDATE()),
(N'Giày Đá Bóng Adidas Predator World Cup Edition (Đinh TF)', N'Giày đá bóng sân cỏ nhân tạo Adidas Predator World Cup cổ thun ôm chân bám sân bứt tốc.', 2200000, 2200000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Giày Đá Bóng World Cup'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Adidas'), GETDATE()),
(N'Giày Đá Bóng Nike Mercurial Vapor World Cup Pack', N'Giày đá bóng Nike Mercurial Vapor TF siêu nhẹ chuyên đá phủi và giải đấu, đệm Air Zoom.', 2400000, 2400000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Giày Đá Bóng World Cup'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Nike'), GETDATE()),
(N'Trái Bóng Đá Tiêu Chuẩn World Cup Al Rihla', N'Quả bóng đá thi đấu World Cup chuẩn FIFA Quality Pro, da PU may tay khép kín bám sân.', 450000, 450000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Phụ Kiện Đá Bóng WC'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Adidas'), GETDATE()),
(N'Tất Đá Bóng Dài World Cup Chống Trượt', N'Tất vớ đá bóng cổ cao hạt silicone chống trượt dưới lòng bàn chân.', 80000, 80000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Phụ Kiện Đá Bóng WC'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Puma'), GETDATE());

-- 6. Thêm Hình Ảnh Sản Phẩm Sắc Nét
INSERT INTO HINH_ANH_SP (ma_san_pham, duong_dan_anh, la_anh_chinh) VALUES 
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Argentina%3 Sao%'), 'https://images.unsplash.com/photo-1577223625816-7546f13df25d?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Bồ Đào Nha%'), 'https://images.unsplash.com/photo-1518091043644-c1d4457512c6?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Pháp%'), 'https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Đức%'), 'https://images.unsplash.com/photo-1522778119026-d647f0596c20?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Quần Đá Bóng ĐT Argentina%'), 'https://images.unsplash.com/photo-1517466787929-bc90951d0974?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Quần Short Đá Bóng ĐT Bồ Đào Nha%'), 'https://images.unsplash.com/photo-1565084888279-aca607ecce0c?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Brazil%'), 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Nhật Bản%'), 'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Predator%'), 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Mercurial%'), 'https://images.unsplash.com/photo-1511556532299-8f662fc26c06?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Al Rihla%'), 'https://images.unsplash.com/photo-1614632537197-38a17061c2bd?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Tất Đá Bóng%'), 'https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=600', 1);

-- 7. Thêm Biến Thể Chi Tiết (Mã SPCT chuẩn CTSP01, CTSP02..., Màu sắc, Kích cỡ S/M/L/XL & Giày 40/41/42)
INSERT INTO CHI_TIET_SAN_PHAM (ma_san_pham, ma_vach_sku, mau_sac, kich_co, so_luong_ton, gia_cong_them, trang_thai) VALUES 
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Argentina%3 Sao%'), 'CTSP01', N'Trắng Xanh', 'S', 50, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Argentina%3 Sao%'), 'CTSP02', N'Trắng Xanh', 'M', 100, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Argentina%3 Sao%'), 'CTSP03', N'Trắng Xanh', 'L', 80, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Argentina%3 Sao%'), 'CTSP04', N'Trắng Xanh', 'XL', 40, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Bồ Đào Nha%'), 'CTSP05', N'Đỏ Xanh', 'M', 60, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Bồ Đào Nha%'), 'CTSP06', N'Đỏ Xanh', 'L', 70, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Pháp%'), 'CTSP07', N'Xanh Navy', 'M', 50, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Pháp%'), 'CTSP08', N'Xanh Navy', 'L', 60, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Đức%'), 'CTSP09', N'Trắng Đen', 'M', 40, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Đức%'), 'CTSP10', N'Trắng Đen', 'L', 45, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Quần Đá Bóng ĐT Argentina%'), 'CTSP11', N'Đen', 'M', 100, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Quần Đá Bóng ĐT Argentina%'), 'CTSP12', N'Đen', 'L', 120, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Quần Short Đá Bóng ĐT Bồ Đào Nha%'), 'CTSP13', N'Xanh Đen', 'M', 80, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Quần Short Đá Bóng ĐT Bồ Đào Nha%'), 'CTSP14', N'Xanh Đen', 'L', 90, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Brazil%'), 'CTSP15', N'Vàng Xanh', 'M', 50, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Brazil%'), 'CTSP16', N'Vàng Xanh', 'L', 60, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Nhật Bản%'), 'CTSP17', N'Xanh Lam', 'M', 40, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Nhật Bản%'), 'CTSP18', N'Xanh Lam', 'L', 50, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Predator%'), 'CTSP19', N'Trắng Đỏ', '40', 20, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Predator%'), 'CTSP20', N'Trắng Đỏ', '41', 25, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Predator%'), 'CTSP21', N'Trắng Đỏ', '42', 30, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Mercurial%'), 'CTSP22', N'Hồng Vàng', '41', 15, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Mercurial%'), 'CTSP23', N'Hồng Vàng', '42', 20, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Al Rihla%'), 'CTSP24', N'Trắng Đa Sắc', 'Size 5', 200, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Tất Đá Bóng%'), 'CTSP25', N'Đen', 'Freesize', 300, 0, N'Đang bán'),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham LIKE N'%Tất Đá Bóng%'), 'CTSP26', N'Trắng', 'Freesize', 300, 0, N'Đang bán');

-- 8. Thêm Khuyến Mãi World Cup
INSERT INTO KHUYEN_MAI (ma_code, phan_tram_giam, so_tien_giam, don_toi_thieu, ngay_bat_dau, ngay_ket_thuc, so_luong_dung) VALUES
('WORLDCUP2026', 15, 100000, 300000, '2024-01-01', '2030-12-31', 1000),
('MESSI10', 10, 50000, 200000, '2024-01-01', '2030-12-31', 500);
GO
GO

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

IF COL_LENGTH('YEU_CAU_HOAN_HANG', 'ly_do_tu_choi') IS NULL
    ALTER TABLE YEU_CAU_HOAN_HANG ADD ly_do_tu_choi NVARCHAR(MAX) NULL;

-- Nâng cấp kiểu dữ liệu tiền tệ từ DECIMAL(10,2) lên DECIMAL(18,2) để tránh lỗi tràn số
ALTER TABLE SAN_PHAM ALTER COLUMN gia_goc DECIMAL(18,2);
ALTER TABLE SAN_PHAM ALTER COLUMN gia_khuyen_mai DECIMAL(18,2);
ALTER TABLE CHI_TIET_SAN_PHAM ALTER COLUMN gia_cong_them DECIMAL(18,2);
ALTER TABLE DON_HANG ALTER COLUMN tong_tien DECIMAL(18,2);
ALTER TABLE DON_HANG ALTER COLUMN phi_ship DECIMAL(18,2);
ALTER TABLE CHI_TIET_DON_HANG ALTER COLUMN don_gia DECIMAL(18,2);
ALTER TABLE YEU_CAU_HOAN_HANG ALTER COLUMN so_tien_hoan DECIMAL(18,2);
GO