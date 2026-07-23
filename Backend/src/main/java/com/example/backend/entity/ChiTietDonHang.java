package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;

@Entity
@Table(name = "CHI_TIET_DON_HANG")
@Getter
@Setter
public class ChiTietDonHang implements Persistable<Integer> {

    @Id
    @Column(name = "ma_ct_don_hang")
    private Integer maCtDonHang;

    @Transient
    private boolean newEntity = true;

    @Override
    public Integer getId() {
        return maCtDonHang;
    }

    @Override
    public boolean isNew() {
        return newEntity;
    }

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.newEntity = false;
    }

    @Column(name = "ma_don_hang")
    private Integer maDonHang;

    @Column(name = "ma_chi_tiet_sp")
    private Integer maChiTietSp;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "don_gia")
    private BigDecimal donGia;
}


