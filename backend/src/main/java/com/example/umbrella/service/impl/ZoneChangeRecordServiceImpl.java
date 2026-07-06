package com.example.umbrella.service.impl;

import com.example.umbrella.dto.ZoneChangeRecordDTO;
import com.example.umbrella.entity.Umbrella;
import com.example.umbrella.entity.Zone;
import com.example.umbrella.entity.ZoneChangeRecord;
import com.example.umbrella.repository.ZoneChangeRecordRepository;
import com.example.umbrella.service.ZoneChangeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZoneChangeRecordServiceImpl implements ZoneChangeRecordService {
    
    @Autowired
    private ZoneChangeRecordRepository recordRepository;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void createRecord(Umbrella umbrella, Zone oldZone, Zone newZone, String changeType, String operator, String changeReason) {
        ZoneChangeRecord record = new ZoneChangeRecord();
        record.setUmbrellaId(umbrella.getId());
        record.setUmbrellaCode(umbrella.getUmbrellaCode());
        
        if (oldZone != null) {
            record.setOldZoneId(oldZone.getId());
            record.setOldZoneName(oldZone.getZoneName());
        }
        
        record.setNewZoneId(newZone.getId());
        record.setNewZoneName(newZone.getZoneName());
        record.setChangeType(changeType);
        record.setOperator(operator != null ? operator : "system");
        record.setChangeReason(changeReason);
        
        recordRepository.save(record);
    }
    
    @Override
    public List<ZoneChangeRecordDTO> getRecordsByUmbrellaId(Long umbrellaId) {
        return recordRepository.findByUmbrellaId(umbrellaId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ZoneChangeRecordDTO> getAllRecords() {
        return recordRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ZoneChangeRecordDTO> getRecordsByZoneId(Long zoneId) {
        List<ZoneChangeRecord> records = recordRepository.findByNewZoneId(zoneId);
        records.addAll(recordRepository.findByOldZoneId(zoneId));
        return records.stream()
                .map(this::convertToDTO)
                .distinct()
                .collect(Collectors.toList());
    }
    
    private ZoneChangeRecordDTO convertToDTO(ZoneChangeRecord record) {
        ZoneChangeRecordDTO dto = new ZoneChangeRecordDTO();
        dto.setId(record.getId());
        dto.setUmbrellaId(record.getUmbrellaId());
        dto.setUmbrellaCode(record.getUmbrellaCode());
        dto.setOldZoneId(record.getOldZoneId());
        dto.setOldZoneName(record.getOldZoneName());
        dto.setNewZoneId(record.getNewZoneId());
        dto.setNewZoneName(record.getNewZoneName());
        dto.setChangeType(record.getChangeType());
        dto.setOperator(record.getOperator());
        dto.setChangeReason(record.getChangeReason());
        dto.setCreatedAt(record.getCreatedAt() != null ? DATE_FORMAT.format(record.getCreatedAt()) : null);
        return dto;
    }
}