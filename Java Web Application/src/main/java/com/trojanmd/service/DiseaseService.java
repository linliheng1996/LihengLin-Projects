package com.trojanmd.service;

import com.trojanmd.domain.BodySystem;
import com.trojanmd.domain.Disease;
import com.trojanmd.domain.Symptom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DiseaseService {

    Disease save(Disease disease);

    Disease get(Long id);

    Disease getByName(String name);

    Page<Disease> listDiseases(Pageable pageable);

    List<Disease> listDiseasesByBodySystem(BodySystem bodySystem);

    List<Disease> listDiseases();

    List<Disease> listDiseases(String ids);

    Disease update(Long id, Disease disease);

    void delete(Long id);
}
