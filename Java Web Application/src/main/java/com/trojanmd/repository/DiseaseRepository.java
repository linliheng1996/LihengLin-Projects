package com.trojanmd.repository;

import com.trojanmd.domain.BodySystem;
import com.trojanmd.domain.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    Disease findByName(String name);
    List<Disease> findAllByBodySystem(BodySystem bodySystem);
}
