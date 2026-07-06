package com.example.umbrella.controller;

import com.example.umbrella.dto.ZoneChangeRecordDTO;
import com.example.umbrella.service.ZoneChangeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zone-change-record")
public class ZoneChangeRecordController {
    
    @Autowired
    private ZoneChangeRecordService recordService;
    
    @GetMapping
    public ResponseEntity<List<ZoneChangeRecordDTO>> getAllRecords() {
        return ResponseEntity.ok(recordService.getAllRecords());
    }
    
    @GetMapping("/umbrella/{umbrellaId}")
    public ResponseEntity<List<ZoneChangeRecordDTO>> getRecordsByUmbrellaId(@PathVariable Long umbrellaId) {
        return ResponseEntity.ok(recordService.getRecordsByUmbrellaId(umbrellaId));
    }
    
    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<List<ZoneChangeRecordDTO>> getRecordsByZoneId(@PathVariable Long zoneId) {
        return ResponseEntity.ok(recordService.getRecordsByZoneId(zoneId));
    }
}