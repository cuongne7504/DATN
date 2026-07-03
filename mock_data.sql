-- Xóa dữ liệu cũ nếu có
DELETE FROM LICH_SU_THANH_TOAN;
DELETE FROM CHI_TIET_GIO_HANG;
DELETE FROM GIO_HANG;
DELETE FROM CHI_TIET_DON_HANG;
DELETE FROM DANH_GIA;
DELETE FROM SAN_PHAM_YEU_THICH;
DELETE FROM DON_HANG;
DELETE FROM HINH_ANH_SP;
DELETE FROM CHI_TIET_SAN_PHAM;
DELETE FROM SAN_PHAM;
DELETE FROM DANH_MUC;
DELETE FROM THUONG_HIEU;
DELETE FROM NGUOI_DUNG WHERE ma_quyen != 1; -- Giữ lại admin

-- Thêm Danh Mục
INSERT INTO DANH_MUC (ten_danh_muc, hinh_anh) VALUES 
(N'Áo Thể Thao', ''),
(N'Quần Thể Thao', ''),
(N'Giày Thể Thao', ''),
(N'Phụ Kiện', '');

-- Thêm Thương Hiệu
INSERT INTO THUONG_HIEU (ten_thuong_hieu, logo) VALUES 
('Nike', ''),
('Adidas', ''),
('Puma', ''),
('Under Armour', '');

-- Thêm Sản Phẩm
INSERT INTO SAN_PHAM (ten_san_pham, mo_ta, gia_goc, gia_khuyen_mai, ma_danh_muc, ma_thuong_hieu, ngay_tao) VALUES 
(N'Áo chạy bộ Nike Dri-FIT', N'Áo thun chạy bộ thoáng khí, thấm hút mồ hôi cực tốt.', 500000, 450000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Áo Thể Thao'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Nike'), GETDATE()),
(N'Quần short thể thao Adidas', N'Quần thể thao cạp chun thoải mái cho mọi hoạt động.', 400000, 350000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Quần Thể Thao'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Adidas'), GETDATE()),
(N'Giày chạy bộ Puma Ultraride', N'Giày siêu nhẹ dành cho chạy bộ chuyên nghiệp.', 2500000, 2000000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Giày Thể Thao'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Puma'), GETDATE()),
(N'Mũ lưỡi trai Under Armour', N'Mũ thể thao chống nắng, form cứng cáp.', 300000, 250000, (SELECT TOP 1 ma_danh_muc FROM DANH_MUC WHERE ten_danh_muc = N'Phụ Kiện'), (SELECT TOP 1 ma_thuong_hieu FROM THUONG_HIEU WHERE ten_thuong_hieu = 'Under Armour'), GETDATE());

-- Thêm Hình Ảnh Sản Phẩm
INSERT INTO HINH_ANH_SP (ma_san_pham, duong_dan_anh, la_anh_chinh) VALUES 
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Áo chạy bộ Nike Dri-FIT'), 'https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Quần short thể thao Adidas'), 'https://images.unsplash.com/photo-1565084888279-aca607ecce0c?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Giày chạy bộ Puma Ultraride'), 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', 1),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Mũ lưỡi trai Under Armour'), 'https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=600', 1);

-- Thêm Chi Tiết Sản Phẩm (Biến thể)
INSERT INTO CHI_TIET_SAN_PHAM (ma_san_pham, mau_sac, kich_co, so_luong_ton) VALUES 
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Áo chạy bộ Nike Dri-FIT'), N'Đen', 'M', 50),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Áo chạy bộ Nike Dri-FIT'), N'Đen', 'L', 30),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Áo chạy bộ Nike Dri-FIT'), N'Trắng', 'M', 20),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Quần short thể thao Adidas'), N'Đen', 'L', 40),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Giày chạy bộ Puma Ultraride'), N'Đỏ', '42', 15),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Giày chạy bộ Puma Ultraride'), N'Đen', '43', 10),
((SELECT TOP 1 ma_san_pham FROM SAN_PHAM WHERE ten_san_pham = N'Mũ lưỡi trai Under Armour'), N'Xám', 'Freesize', 100);

-- Thêm Nhân viên
INSERT INTO NGUOI_DUNG (ho_ten, email, so_dien_thoai, dia_chi, mat_khau, ma_quyen, ngay_tao) VALUES 
(N'Trần Nhân Viên 1', 'nhanvien1@sportpro.com', '0988888881', N'Hà Nội', '$2a$10$t/do9MfRTY4PtTpd/cP/5.R5b43jwfadEjvLpCn.7iWI4fgRceZgW', 2, GETDATE()),
(N'Lê Nhân Viên 2', 'nhanvien2@sportpro.com', '0988888882', N'Hồ Chí Minh', '$2a$10$t/do9MfRTY4PtTpd/cP/5.R5b43jwfadEjvLpCn.7iWI4fgRceZgW', 2, GETDATE());

-- Thêm Khách Hàng 
INSERT INTO NGUOI_DUNG (ho_ten, email, so_dien_thoai, dia_chi, mat_khau, ma_quyen, ngay_tao) VALUES 
(N'Nguyễn Văn Khách', 'khachhang@sportpro.com', '0912345678', N'Hà Nội', '$2a$10$t/do9MfRTY4PtTpd/cP/5.R5b43jwfadEjvLpCn.7iWI4fgRceZgW', 3, GETDATE()),
(N'Phạm Thị Khách Hàng 2', 'khachhang2@sportpro.com', '0912345679', N'Đà Nẵng', '$2a$10$t/do9MfRTY4PtTpd/cP/5.R5b43jwfadEjvLpCn.7iWI4fgRceZgW', 3, GETDATE());

-- Thêm Đơn Hàng Mẫu 
INSERT INTO DON_HANG (ma_nguoi_dung, ma_nhan_vien, trang_thai, tong_tien, dia_chi_giao, phuong_thuc_tt, ngay_dat) VALUES 
((SELECT TOP 1 ma_nguoi_dung FROM NGUOI_DUNG WHERE email = 'khachhang@sportpro.com'), (SELECT TOP 1 ma_nguoi_dung FROM NGUOI_DUNG WHERE email = 'nhanvien1@sportpro.com'), 3, 450000, N'Hà Nội', N'COD', DATEADD(day, -5, GETDATE())),
((SELECT TOP 1 ma_nguoi_dung FROM NGUOI_DUNG WHERE email = 'khachhang@sportpro.com'), (SELECT TOP 1 ma_nguoi_dung FROM NGUOI_DUNG WHERE email = 'nhanvien2@sportpro.com'), 3, 2000000, N'Hà Nội', N'VNPAY', DATEADD(day, -2, GETDATE()));

-- Thêm Chi Tiết Đơn Hàng
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

-- Fix loi font tieng Viet
UPDATE SAN_PHAM SET ten_san_pham = N'Áo thể thao nam mùa hè' WHERE ma_san_pham = 1;
UPDATE SAN_PHAM SET ten_san_pham = N'Giày chạy bộ Nike Air Zoom Pegasus 40' WHERE ma_san_pham = 2;
UPDATE SAN_PHAM SET ten_san_pham = N'Áo đội tuyển Việt Nam sân nhà', mo_ta = N'Áo cổ vũ đội tuyển quốc gia.' WHERE ma_san_pham = 3;
UPDATE SAN_PHAM SET ten_san_pham = N'Găng tay mới' WHERE ma_san_pham = 4;
UPDATE CHI_TIET_SAN_PHAM SET mau_sac = N'Đỏ' WHERE ma_chi_tiet_sp IN (1, 2, 5);
UPDATE CHI_TIET_SAN_PHAM SET mau_sac = N'Đen' WHERE ma_chi_tiet_sp = 3;
UPDATE CHI_TIET_SAN_PHAM SET mau_sac = N'Trắng' WHERE ma_chi_tiet_sp IN (4, 6);
