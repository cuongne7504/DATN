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

@Entity
@Table(name = "HINH_ANH_SP")
@Getter
@Setter
public class HinhAnhSp implements Persistable<Integer> {

    @Id
    @Column(name = "ma_hinh_anh")
    private Integer maHinhAnh;

    @Transient
    private boolean newEntity = true;

    @Override
    public Integer getId() {
        return maHinhAnh;
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

    @Column(name = "ma_san_pham")
    private Integer maSanPham;

    @Column(name = "duong_dan_anh")
    private String duongDanAnh;

    @Column(name = "la_anh_chinh")
    private Boolean laAnhChinh;
}
