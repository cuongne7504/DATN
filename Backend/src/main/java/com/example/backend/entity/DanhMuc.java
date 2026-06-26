package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "DANH_MUC")
@Getter
@Setter
public class DanhMuc {

    @Id
    @Column(name = "ma_danh_muc")
    private Integer maDanhMuc;

    @Column(name = "ten_danh_muc")
    private String tenDanhMuc;
}
