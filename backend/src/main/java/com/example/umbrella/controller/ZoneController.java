package com.example.umbrella.controller;

import com.example.umbrella.dto.ZoneDTO;
import com.example.umbrella.service.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/zone")
public class ZoneController {
    
    @Autowired
    private ZoneService zoneService;
    
    @PostMapping
    public ResponseEntity<?> createZone(@RequestBody ZoneDTO dto) {
        try {
            ZoneDTO result = zoneService.createZone(dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getZoneById(@PathVariable Long id) {
        try {
            ZoneDTO result = zoneService.getZoneById(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<List<ZoneDTO>> getAllZones() {
        return ResponseEntity.ok(zoneService.getAllZones());
    }
    
    @GetMapping("/tree")
    public ResponseEntity<List<ZoneDTO>> getZoneTree() {
        return ResponseEntity.ok(zoneService.getZoneTree());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateZone(@PathVariable Long id, @RequestBody ZoneDTO dto) {
        try {
            ZoneDTO result = zoneService.updateZone(id, dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteZone(@PathVariable Long id) {
        try {
            zoneService.deleteZone(id);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}