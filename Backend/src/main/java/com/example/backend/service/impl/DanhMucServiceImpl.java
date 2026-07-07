package com.example.backend.service.impl;

import com.example.backend.entity.DanhMuc;
import com.example.backend.repository.DanhMucRepository;
import com.example.backend.service.DanhMucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DanhMucServiceImpl implements DanhMucService {

    @Autowired
    private DanhMucRepository danhMucRepository;

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
        danhMucRepository.deleteById(id);
    }
}
