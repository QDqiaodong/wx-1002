package com.example.umbrella.service.impl;

import com.example.umbrella.dto.UmbrellaDTO;
import com.example.umbrella.dto.request.BatchAssignRequest;
import com.example.umbrella.entity.Umbrella;
import com.example.umbrella.entity.Zone;
import com.example.umbrella.repository.UmbrellaRepository;
import com.example.umbrella.service.UmbrellaService;
import com.example.umbrella.service.ZoneChangeRecordService;
import com.example.umbrella.service.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UmbrellaServiceImpl implements UmbrellaService {
    
    @Autowired
    private UmbrellaRepository umbrellaRepository;
    
    @Autowired
    private ZoneService zoneService;
    
    @Autowired
    private ZoneChangeRecordService zoneChangeRecordService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String UMBRELLA_SIZE_KEY = "umbrella:size:specs";
    
    @Override
    @Transactional
    public UmbrellaDTO createUmbrella(UmbrellaDTO dto) {
        if (umbrellaRepository.existsByUmbrellaCode(dto.getUmbrellaCode())) {
            throw new RuntimeException("伞体编号已存在");
        }
        
        Umbrella umbrella = new Umbrella();
        umbrella.setUmbrellaCode(dto.getUmbrellaCode());
        umbrella.setSize(dto.getSize());
        umbrella.setMaterial(dto.getMaterial());
        umbrella.setZoneId(dto.getZoneId());
        umbrella.setStatus(dto.getStatus());
        umbrella.setRemarks(dto.getRemarks());
        
        Umbrella saved = umbrellaRepository.save(umbrella);
        
        if (dto.getSize() != null) {
            redisTemplate.opsForZSet().add(UMBRELLA_SIZE_KEY, dto.getSize(), 0);
        }
        
        if (dto.getZoneId() != null) {
            Zone zone = zoneService.findById(dto.getZoneId());
            zoneChangeRecordService.createRecord(saved, null, zone, "init", null, "初始绑定");
        }
        
        return convertToDTO(saved);
    }
    
    @Override
    @Transactional
    public UmbrellaDTO updateUmbrella(Long id, UmbrellaDTO dto) {
        Umbrella umbrella = umbrellaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("遮阳伞不存在"));
        
        if (!umbrella.getUmbrellaCode().equals(dto.getUmbrellaCode()) 
                && umbrellaRepository.existsByUmbrellaCode(dto.getUmbrellaCode())) {
            throw new RuntimeException("伞体编号已存在");
        }
        
        umbrella.setUmbrellaCode(dto.getUmbrellaCode());
        umbrella.setSize(dto.getSize());
        umbrella.setMaterial(dto.getMaterial());
        umbrella.setStatus(dto.getStatus());
        umbrella.setRemarks(dto.getRemarks());
        
        Umbrella saved = umbrellaRepository.save(umbrella);
        return convertToDTO(saved);
    }
    
    @Override
    @Transactional
    public void deleteUmbrella(Long id) {
        if (!umbrellaRepository.existsById(id)) {
            throw new RuntimeException("遮阳伞不存在");
        }
        umbrellaRepository.deleteById(id);
    }
    
    @Override
    public UmbrellaDTO getUmbrellaById(Long id) {
        Umbrella umbrella = umbrellaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("遮阳伞不存在"));
        return convertToDTO(umbrella);
    }
    
    @Override
    public List<UmbrellaDTO> getAllUmbrellas() {
        return umbrellaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UmbrellaDTO> getUmbrellasByZoneId(Long zoneId) {
        return umbrellaRepository.findByZoneId(zoneId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UmbrellaDTO> getUmbrellasWithoutZone() {
        return umbrellaRepository.findByZoneIdIsNull().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void assignZone(Long umbrellaId, Long zoneId, String operator, String changeReason) {
        Umbrella umbrella = umbrellaRepository.findById(umbrellaId)
                .orElseThrow(() -> new RuntimeException("遮阳伞不存在"));
        
        Zone oldZone = umbrella.getZoneId() != null ? zoneService.findById(umbrella.getZoneId()) : null;
        Zone newZone = zoneService.findById(zoneId);
        
        if (newZone.getCapacity() != null) {
            long currentCount = umbrellaRepository.findByZoneId(zoneId).size();
            if (oldZone != null && oldZone.getId().equals(zoneId)) {
                currentCount--;
            }
            if (currentCount >= newZone.getCapacity()) {
                throw new RuntimeException("目标片区[" + newZone.getZoneName() + "]容量已满，当前已占用: " + currentCount + "，容量上限: " + newZone.getCapacity());
            }
        }
        
        umbrella.setZoneId(zoneId);
        umbrellaRepository.save(umbrella);
        
        zoneChangeRecordService.createRecord(umbrella, oldZone, newZone, "single", operator, changeReason);
    }
    
    @Override
    @Transactional
    public void batchAssignZone(BatchAssignRequest request) {
        Zone newZone = zoneService.findById(request.getZoneId());
        
        if (newZone.getCapacity() != null) {
            long currentCount = umbrellaRepository.findByZoneId(request.getZoneId()).size();
            
            int newAssignCount = 0;
            for (Long umbrellaId : request.getUmbrellaIds()) {
                Umbrella umbrella = umbrellaRepository.findById(umbrellaId)
                        .orElseThrow(() -> new RuntimeException("遮阳伞不存在: " + umbrellaId));
                if (umbrella.getZoneId() == null || !umbrella.getZoneId().equals(request.getZoneId())) {
                    newAssignCount++;
                }
            }
            
            if (currentCount + newAssignCount > newZone.getCapacity()) {
                throw new RuntimeException("目标片区[" + newZone.getZoneName() + "]容量不足，已占用: " + currentCount + "，剩余: " + (newZone.getCapacity() - currentCount) + "，待分配: " + newAssignCount + "，超额: " + (currentCount + newAssignCount - newZone.getCapacity()));
            }
        }
        
        for (Long umbrellaId : request.getUmbrellaIds()) {
            Umbrella umbrella = umbrellaRepository.findById(umbrellaId)
                    .orElseThrow(() -> new RuntimeException("遮阳伞不存在: " + umbrellaId));
            
            Zone oldZone = umbrella.getZoneId() != null ? zoneService.findById(umbrella.getZoneId()) : null;
            
            umbrella.setZoneId(request.getZoneId());
            umbrellaRepository.save(umbrella);
            
            zoneChangeRecordService.createRecord(umbrella, oldZone, newZone, "batch", request.getOperator(), request.getChangeReason());
        }
    }
    
    @Override
    public Umbrella findById(Long id) {
        return umbrellaRepository.findById(id).orElse(null);
    }
    
    private UmbrellaDTO convertToDTO(Umbrella umbrella) {
        UmbrellaDTO dto = new UmbrellaDTO();
        dto.setId(umbrella.getId());
        dto.setUmbrellaCode(umbrella.getUmbrellaCode());
        dto.setSize(umbrella.getSize());
        dto.setMaterial(umbrella.getMaterial());
        dto.setZoneId(umbrella.getZoneId());
        
        if (umbrella.getZoneId() != null) {
            Zone zone = zoneService.findById(umbrella.getZoneId());
            if (zone != null) {
                dto.setZoneName(zone.getZoneName());
            }
        }
        
        dto.setStatus(umbrella.getStatus());
        dto.setRemarks(umbrella.getRemarks());
        return dto;
    }
}