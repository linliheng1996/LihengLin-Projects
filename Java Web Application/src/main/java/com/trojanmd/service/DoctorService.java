package com.trojanmd.service;

import com.trojanmd.domain.Disease;
import com.trojanmd.domain.Doctor;
import com.trojanmd.domain.Symptom;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface DoctorService{
    List<Doctor> findAllDoctors();

//    List<Doctor> listDoctorsBySymptom(Symptom symptom);

    List<Doctor> listDoctorsByDisease(Disease disease);

    Doctor findByEmail(String email);

    Doctor get(Long id);

    Doctor update(Long id, Doctor doctor);

    void save(Doctor doctor);

}
