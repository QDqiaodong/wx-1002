package com.example.umbrella.repository;

import com.example.umbrella.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    
    List<Zone> findByParentId(Long parentId);
    
    List<Zone> findByLevel(Integer level);
    
    List<Zone> findByStatus(String status);
    
    List<Zone> findByParentIdIsNull();
    
    List<Zone> findByParentIdOrderBySortOrder(Long parentId);
}