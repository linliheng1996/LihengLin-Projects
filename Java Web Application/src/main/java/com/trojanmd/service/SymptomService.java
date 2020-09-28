package com.trojanmd.service;

import com.trojanmd.domain.BodyPart;
import com.trojanmd.domain.Symptom;
import com.trojanmd.vo.SymptomQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface SymptomService {

    Symptom save(Symptom symptom);

    Symptom get(Long id);

    Symptom getByName(String name);

    Page<Symptom> listSymptoms(Pageable pageable);

    Page<Symptom> listSymptoms(Pageable pageable, SymptomQuery symptomQuery);

    List<Symptom> listSymptomsByBodyPart(BodyPart bodyPart);

    List<Symptom> listSymptoms();

    List<Symptom> listSymptoms(String ids);

    Symptom update(Long id, Symptom symptom);

    void delete(Long id);
}
