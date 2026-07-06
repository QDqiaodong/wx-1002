package com.example.umbrella.dto;

import lombok.Data;

import java.util.List;

@Data
public class ZoneDTO {
    private Long id;
    private String zoneName;
    private Long parentId;
    private Integer level;
    private Integer sortOrder;
    private String status;
    private String remarks;
    private Integer umbrellaCount;
    private List<ZoneDTO> children;
}