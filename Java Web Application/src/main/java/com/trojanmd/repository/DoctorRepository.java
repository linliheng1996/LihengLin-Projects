package com.trojanmd.repository;

import com.trojanmd.domain.Disease;
import com.trojanmd.domain.Doctor;
import com.trojanmd.domain.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findAll();

//    List<Doctor> findAllBySymptoms(Symptom symptom);

    List<Doctor> findAllByDiseases(Disease disease);

    Doctor findByEmail(String email);
}
