package com.example.umbrella.controller;

import com.example.umbrella.dto.UmbrellaDTO;
import com.example.umbrella.dto.request.BatchAssignRequest;
import com.example.umbrella.service.UmbrellaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/umbrella")
public class UmbrellaController {
    
    @Autowired
    private UmbrellaService umbrellaService;
    
    @PostMapping
    public ResponseEntity<?> createUmbrella(@RequestBody UmbrellaDTO dto) {
        try {
            UmbrellaDTO result = umbrellaService.createUmbrella(dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUmbrellaById(@PathVariable Long id) {
        try {
            UmbrellaDTO result = umbrellaService.getUmbrellaById(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<List<UmbrellaDTO>> getAllUmbrellas() {
        return ResponseEntity.ok(umbrellaService.getAllUmbrellas());
    }
    
    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<List<UmbrellaDTO>> getUmbrellasByZoneId(@PathVariable Long zoneId) {
        return ResponseEntity.ok(umbrellaService.getUmbrellasByZoneId(zoneId));
    }
    
    @GetMapping("/without-zone")
    public ResponseEntity<List<UmbrellaDTO>> getUmbrellasWithoutZone() {
        return ResponseEntity.ok(umbrellaService.getUmbrellasWithoutZone());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUmbrella(@PathVariable Long id, @RequestBody UmbrellaDTO dto) {
        try {
            UmbrellaDTO result = umbrellaService.updateUmbrella(id, dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUmbrella(@PathVariable Long id) {
        try {
            umbrellaService.deleteUmbrella(id);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/assign-zone")
    public ResponseEntity<?> assignZone(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Long zoneId = ((Number) request.get("zoneId")).longValue();
            String operator = (String) request.get("operator");
            String changeReason = (String) request.get("changeReason");
            umbrellaService.assignZone(id, zoneId, operator, changeReason);
            return ResponseEntity.ok(Map.of("message", "分配成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/batch-assign")
    public ResponseEntity<?> batchAssignZone(@RequestBody BatchAssignRequest request) {
        try {
            umbrellaService.batchAssignZone(request);
            return ResponseEntity.ok(Map.of("message", "批量分配成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}