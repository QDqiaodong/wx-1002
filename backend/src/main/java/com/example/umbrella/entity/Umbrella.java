package com.example.umbrella.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "umbrella")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Umbrella {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "umbrella_code", unique = true, nullable = false, length = 50)
    private String umbrellaCode;
    
    @Column(name = "size", length = 20)
    private String size;
    
    @Column(name = "material", length = 50)
    private String material;
    
    @Column(name = "zone_id")
    private Long zoneId;
    
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
            status = "normal";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new java.util.Date();
    }
}