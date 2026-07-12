package com.example.umbrella.service.impl;

import com.example.umbrella.dto.ZoneDTO;
import com.example.umbrella.entity.Umbrella;
import com.example.umbrella.entity.Zone;
import com.example.umbrella.repository.UmbrellaRepository;
import com.example.umbrella.repository.ZoneRepository;
import com.example.umbrella.service.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ZoneServiceImpl implements ZoneService {
    
    @Autowired
    private ZoneRepository zoneRepository;
    
    @Autowired
    private UmbrellaRepository umbrellaRepository;
    
    @Override
    @Transactional
    public ZoneDTO createZone(ZoneDTO dto) {
        Zone zone = new Zone();
        zone.setZoneName(dto.getZoneName());
        zone.setParentId(dto.getParentId());
        zone.setLevel(dto.getLevel());
        zone.setSortOrder(dto.getSortOrder());
        zone.setStatus(dto.getStatus());
        zone.setRemarks(dto.getRemarks());
        zone.setCapacity(dto.getCapacity());
        
        if (zone.getParentId() != null) {
            Zone parent = zoneRepository.findById(zone.getParentId())
                    .orElseThrow(() -> new RuntimeException("父片区不存在"));
            zone.setLevel(parent.getLevel() + 1);
        }
        
        Zone saved = zoneRepository.save(zone);
        return convertToDTO(saved);
    }
    
    @Override
    @Transactional
    public ZoneDTO updateZone(Long id, ZoneDTO dto) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("片区不存在"));
        
        zone.setZoneName(dto.getZoneName());
        zone.setParentId(dto.getParentId());
        zone.setSortOrder(dto.getSortOrder());
        zone.setStatus(dto.getStatus());
        zone.setRemarks(dto.getRemarks());
        zone.setCapacity(dto.getCapacity());
        
        Zone saved = zoneRepository.save(zone);
        return convertToDTO(saved);
    }
    
    @Override
    @Transactional
    public void deleteZone(Long id) {
        if (!zoneRepository.existsById(id)) {
            throw new RuntimeException("片区不存在");
        }
        
        List<Zone> children = zoneRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("该片区存在子片区，无法删除");
        }
        
        long umbrellaCount = umbrellaRepository.findByZoneId(id).size();
        if (umbrellaCount > 0) {
            throw new RuntimeException("该片区下存在遮阳伞，无法删除");
        }
        
        zoneRepository.deleteById(id);
    }
    
    @Override
    public ZoneDTO getZoneById(Long id) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("片区不存在"));
        return convertToDTO(zone);
    }
    
    @Override
    public List<ZoneDTO> getAllZones() {
        Map<Long, Long> umbrellaCountMap = umbrellaRepository.findAll().stream()
                .filter(u -> u.getZoneId() != null)
                .collect(Collectors.groupingBy(Umbrella::getZoneId, Collectors.counting()));
        
        return zoneRepository.findAll().stream()
                .map(z -> {
                    ZoneDTO dto = convertToDTO(z);
                    dto.setUmbrellaCount(umbrellaCountMap.getOrDefault(z.getId(), 0L).intValue());
                    if (z.getCapacity() != null) {
                        dto.setRemainingCapacity(z.getCapacity() - dto.getUmbrellaCount());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ZoneDTO> getZoneTree() {
        List<Zone> allZones = zoneRepository.findAll();
        
        Map<Long, List<Zone>> childrenMap = allZones.stream()
                .filter(z -> z.getParentId() != null)
                .collect(Collectors.groupingBy(Zone::getParentId));
        
        Map<Long, Long> umbrellaCountMap = umbrellaRepository.findAll().stream()
                .filter(u -> u.getZoneId() != null)
                .collect(Collectors.groupingBy(Umbrella::getZoneId, Collectors.counting()));
        
        List<ZoneDTO> rootZones = allZones.stream()
                .filter(z -> z.getParentId() == null)
                .map(z -> buildTree(z, childrenMap, umbrellaCountMap))
                .collect(Collectors.toList());
        
        return rootZones;
    }
    
    private ZoneDTO buildTree(Zone zone, Map<Long, List<Zone>> childrenMap, Map<Long, Long> umbrellaCountMap) {
        ZoneDTO dto = convertToDTO(zone);
        int count = umbrellaCountMap.getOrDefault(zone.getId(), 0L).intValue();
        dto.setUmbrellaCount(count);
        
        if (zone.getCapacity() != null) {
            dto.setRemainingCapacity(zone.getCapacity() - count);
        }
        
        List<Zone> children = childrenMap.getOrDefault(zone.getId(), new ArrayList<>());
        if (!children.isEmpty()) {
            List<ZoneDTO> childDTOs = children.stream()
                    .map(z -> buildTree(z, childrenMap, umbrellaCountMap))
                    .collect(Collectors.toList());
            dto.setChildren(childDTOs);
        }
        
        return dto;
    }
    
    @Override
    public Zone findById(Long id) {
        return zoneRepository.findById(id).orElse(null);
    }
    
    private ZoneDTO convertToDTO(Zone zone) {
        ZoneDTO dto = new ZoneDTO();
        dto.setId(zone.getId());
        dto.setZoneName(zone.getZoneName());
        dto.setParentId(zone.getParentId());
        dto.setLevel(zone.getLevel());
        dto.setSortOrder(zone.getSortOrder());
        dto.setStatus(zone.getStatus());
        dto.setRemarks(zone.getRemarks());
        dto.setCapacity(zone.getCapacity());
        return dto;
    }
}