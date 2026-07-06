package com.example.umbrella.dto;

import lombok.Data;

@Data
public class ZoneChangeRecordDTO {
    private Long id;
    private Long umbrellaId;
    private String umbrellaCode;
    private Long oldZoneId;
    private String oldZoneName;
    private Long newZoneId;
    private String newZoneName;
    private String changeType;
    private String operator;
    private String changeReason;
    private String createdAt;
}