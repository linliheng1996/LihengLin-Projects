package com.trojanmd.service;

import com.trojanmd.domain.Disease;
import com.trojanmd.domain.Doctor;
import com.trojanmd.domain.Symptom;
import com.trojanmd.repository.DoctorRepository;
import com.trojanmd.utils.MyBeanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService{

    @Autowired
    DoctorRepository doctorRepository;

    @Override
    public List<Doctor> findAllDoctors() {
        return doctorRepository.findAll();
    }

//    @Override
//    public List<Doctor> listDoctorsBySymptom(Symptom symptom) {
//        return doctorRepository.findAllBySymptoms(symptom);
//    }

    @Override
    public List<Doctor> listDoctorsByDisease(Disease disease) {
        return doctorRepository.findAllByDiseases(disease);
    }

    @Override
    public Doctor findByEmail(String email) {
        return doctorRepository.findByEmail(email);
    }

    @Override
    public void save(Doctor doctor) {
        doctorRepository.save(doctor);
    }

    @Override
    public Doctor update(Long id, Doctor doctor) {
        Doctor d = doctorRepository.getOne(id);
        BeanUtils.copyProperties(doctor, d, MyBeanUtils.getNullPropertyNames(doctor));
        d.setActive(true);
        return doctorRepository.save(d);
    }

    @Override
    public Doctor get(Long id) {
        return doctorRepository.getOne(id);
    }
}
