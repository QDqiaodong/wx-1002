package com.example.umbrella.service;

import com.example.umbrella.dto.UmbrellaDTO;
import com.example.umbrella.dto.request.BatchAssignRequest;
import com.example.umbrella.entity.Umbrella;

import java.util.List;

public interface UmbrellaService {
    
    UmbrellaDTO createUmbrella(UmbrellaDTO dto);
    
    UmbrellaDTO updateUmbrella(Long id, UmbrellaDTO dto);
    
    void deleteUmbrella(Long id);
    
    UmbrellaDTO getUmbrellaById(Long id);
    
    List<UmbrellaDTO> getAllUmbrellas();
    
    List<UmbrellaDTO> getUmbrellasByZoneId(Long zoneId);
    
    List<UmbrellaDTO> getUmbrellasWithoutZone();
    
    void assignZone(Long umbrellaId, Long zoneId, String operator, String changeReason);
    
    void batchAssignZone(BatchAssignRequest request);
    
    Umbrella findById(Long id);
}