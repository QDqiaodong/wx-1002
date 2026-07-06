package com.example.umbrella.repository;

import com.example.umbrella.entity.ZoneChangeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneChangeRecordRepository extends JpaRepository<ZoneChangeRecord, Long> {
    
    List<ZoneChangeRecord> findByUmbrellaId(Long umbrellaId);
    
    List<ZoneChangeRecord> findByNewZoneId(Long zoneId);
    
    List<ZoneChangeRecord> findByOldZoneId(Long zoneId);
    
    List<ZoneChangeRecord> findByOperator(String operator);
    
    List<ZoneChangeRecord> findAllByOrderByCreatedAtDesc();
}