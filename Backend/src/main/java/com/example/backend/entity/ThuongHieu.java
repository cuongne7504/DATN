package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "THUONG_HIEU")
@Getter
@Setter
public class ThuongHieu {

    @Id
    @Column(name = "ma_thuong_hieu")
    private Integer maThuongHieu;

    @Column(name = "ten_thuong_hieu")
    private String tenThuongHieu;
}
