package com.example.umbrella.dto;

import lombok.Data;

@Data
public class UmbrellaDTO {
    private Long id;
    private String umbrellaCode;
    private String size;
    private String material;
    private Long zoneId;
    private String zoneName;
    private String status;
    private String remarks;
}