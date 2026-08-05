package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "CHI_TIET_GIO_HANG")
@Getter
@Setter
public class ChiTietGioHang implements Persistable<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ct_gio_hang")
    private Integer maCtGioHang;

    @Transient
    private boolean newEntity = true;

    @Override
    public Integer getId() {
        return maCtGioHang;
    }

    @Override
    public boolean isNew() {
        return newEntity;
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.newEntity = false;
    }

    @Column(name = "ma_gio_hang")
    private Integer maGioHang;

    @Column(name = "ma_chi_tiet_sp")
    private Integer maChiTietSp;

    @Column(name = "so_luong")
    private Integer soLuong;
}
