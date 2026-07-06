package com.example.umbrella.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BatchAssignRequest {
    private List<Long> umbrellaIds;
    private Long zoneId;
    private String operator;
    private String changeReason;
}