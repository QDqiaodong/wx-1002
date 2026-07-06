package com.example.umbrella.service;

import com.example.umbrella.dto.ZoneDTO;
import com.example.umbrella.entity.Zone;

import java.util.List;

public interface ZoneService {
    
    ZoneDTO createZone(ZoneDTO dto);
    
    ZoneDTO updateZone(Long id, ZoneDTO dto);
    
    void deleteZone(Long id);
    
    ZoneDTO getZoneById(Long id);
    
    List<ZoneDTO> getAllZones();
    
    List<ZoneDTO> getZoneTree();
    
    Zone findById(Long id);
}