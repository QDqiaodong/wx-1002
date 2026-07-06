package com.example.umbrella.service;

import com.example.umbrella.dto.ZoneChangeRecordDTO;
import com.example.umbrella.entity.Umbrella;
import com.example.umbrella.entity.Zone;

import java.util.List;

public interface ZoneChangeRecordService {
    
    void createRecord(Umbrella umbrella, Zone oldZone, Zone newZone, String changeType, String operator, String changeReason);
    
    List<ZoneChangeRecordDTO> getRecordsByUmbrellaId(Long umbrellaId);
    
    List<ZoneChangeRecordDTO> getAllRecords();
    
    List<ZoneChangeRecordDTO> getRecordsByZoneId(Long zoneId);
}