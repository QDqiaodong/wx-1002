package com.example.umbrella.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "zone")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Zone {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "zone_name", nullable = false, length = 100)
    private String zoneName;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    @Column(name = "level")
    private Integer level;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    @Column(name = "status", length = 20)
    private String status;
    
    @Column(name = "remarks", length = 200)
    private String remarks;
    
    @Column(name = "created_at")
    private java.util.Date createdAt;
    
    @Column(name = "updated_at")
    private java.util.Date updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new java.util.Date();
        updatedAt = new java.util.Date();
        if (status == null) {
            status = "active";
        }
        if (level == null) {
            level = 1;
        }
        if (sortOrder == null) {
            sortOrder = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new java.util.Date();
    }
}