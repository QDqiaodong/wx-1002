package com.example.umbrella.repository;

import com.example.umbrella.entity.Umbrella;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UmbrellaRepository extends JpaRepository<Umbrella, Long> {
    
    Optional<Umbrella> findByUmbrellaCode(String umbrellaCode);
    
    List<Umbrella> findByZoneId(Long zoneId);
    
    List<Umbrella> findByStatus(String status);
    
    List<Umbrella> findByZoneIdIsNull();
    
    boolean existsByUmbrellaCode(String umbrellaCode);
}