package com.example.backend.service.impl;

import com.example.backend.entity.ThuongHieu;
import com.example.backend.exception.BadRequestException;
import com.example.backend.repository.SanPhamRepository;
import com.example.backend.repository.ThuongHieuRepository;
import com.example.backend.service.ThuongHieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ThuongHieuServiceImpl implements ThuongHieuService {

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Override
    public List<ThuongHieu> getAll() {
        return thuongHieuRepository.findAll();
    }

    @Override
    public Optional<ThuongHieu> getById(Integer id) {
        return thuongHieuRepository.findById(id);
    }

    @Override
    public ThuongHieu save(ThuongHieu thuongHieu) {
        return thuongHieuRepository.save(thuongHieu);
    }

    @Override
    public void delete(Integer id) {
        if (sanPhamRepository.existsByMaThuongHieu(id)) {
            throw new BadRequestException("Không thể xóa thương hiệu này vì đang có sản phẩm thuộc thương hiệu!");
        }
        thuongHieuRepository.deleteById(id);
    }
}
