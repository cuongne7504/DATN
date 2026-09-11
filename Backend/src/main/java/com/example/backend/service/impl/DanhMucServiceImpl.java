package com.example.backend.service.impl;

import com.example.backend.entity.DanhMuc;
import com.example.backend.exception.BadRequestException;
import com.example.backend.repository.DanhMucRepository;
import com.example.backend.repository.SanPhamRepository;
import com.example.backend.service.DanhMucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DanhMucServiceImpl implements DanhMucService {

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Override
    public List<DanhMuc> getAll() {
        return danhMucRepository.findAll();
    }

    @Override
    public Optional<DanhMuc> getById(Integer id) {
        return danhMucRepository.findById(id);
    }

    @Override
    public DanhMuc save(DanhMuc danhMuc) {
        return danhMucRepository.save(danhMuc);
    }

    @Override
    public void delete(Integer id) {
        if (sanPhamRepository.existsByMaDanhMuc(id)) {
            throw new BadRequestException("Không thể xóa danh mục này vì đang có sản phẩm thuộc danh mục!");
        }
        danhMucRepository.deleteById(id);
    }
}
