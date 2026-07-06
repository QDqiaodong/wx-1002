package com.example.umbrella.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "zone_change_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoneChangeRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "umbrella_id", nullable = false)
    private Long umbrellaId;
    
    @Column(name = "umbrella_code", length = 50)
    private String umbrellaCode;
    
    @Column(name = "old_zone_id")
    private Long oldZoneId;
    
    @Column(name = "old_zone_name", length = 100)
    private String oldZoneName;
    
    @Column(name = "new_zone_id", nullable = false)
    private Long newZoneId;
    
    @Column(name = "new_zone_name", length = 100)
    private String newZoneName;
    
    @Column(name = "change_type", length = 20)
    private String changeType;
    
    @Column(name = "operator", length = 50)
    private String operator;
    
    @Column(name = "change_reason", length = 200)
    private String changeReason;
    
    @Column(name = "created_at")
    private java.util.Date createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new java.util.Date();
    }
}