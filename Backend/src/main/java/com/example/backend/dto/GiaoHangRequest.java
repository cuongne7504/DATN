package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GiaoHangRequest {
    private String shipperName;
    private String shipperPhone;
    private String shippingNote;
}
