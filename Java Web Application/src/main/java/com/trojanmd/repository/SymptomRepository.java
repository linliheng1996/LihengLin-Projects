package com.trojanmd.repository;

import com.trojanmd.domain.BodyPart;
import com.trojanmd.domain.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SymptomRepository extends JpaRepository<Symptom, Long>, JpaSpecificationExecutor<Symptom> {
    Symptom findByName(String name);
    List<Symptom> findAllByBodyPart(BodyPart bodyPart);
}
