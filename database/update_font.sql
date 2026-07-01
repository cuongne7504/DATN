UPDATE SAN_PHAM SET ten_san_pham = N'Áo thể thao nam mùa hè' WHERE ma_san_pham = 1;
UPDATE SAN_PHAM SET ten_san_pham = N'Giày chạy bộ Nike Air Zoom Pegasus 40' WHERE ma_san_pham = 2;
UPDATE SAN_PHAM SET ten_san_pham = N'Áo đội tuyển Việt Nam sân nhà', mo_ta = N'Áo cổ vũ đội tuyển quốc gia.' WHERE ma_san_pham = 3;
UPDATE SAN_PHAM SET ten_san_pham = N'Găng tay mới' WHERE ma_san_pham = 4;
UPDATE CHI_TIET_SAN_PHAM SET mau_sac = N'Đỏ' WHERE ma_chi_tiet_sp IN (1, 2, 5);
UPDATE CHI_TIET_SAN_PHAM SET mau_sac = N'Đen' WHERE ma_chi_tiet_sp = 3;
UPDATE CHI_TIET_SAN_PHAM SET mau_sac = N'Trắng' WHERE ma_chi_tiet_sp IN (4, 6);
