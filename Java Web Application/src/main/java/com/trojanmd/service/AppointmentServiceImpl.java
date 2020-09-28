package com.trojanmd.service;

import com.trojanmd.domain.Appointment;
import com.trojanmd.domain.Doctor;
import com.trojanmd.domain.User;
import com.trojanmd.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService{

    @Autowired
    AppointmentRepository appointmentRepository;

    @Override
    public List<Appointment> findAllByDateAndDoctorIdAndIsAvailable(Date date, Long id, Boolean isAvailable) {
        List<Appointment> apps;
        apps = appointmentRepository.findAllByDateAndDoctorIdAndIsAvailable(date, id, isAvailable);
        return apps;
    }

    @Override
    public List<Appointment> findAllByDateAndDoctorId(Date date, Long id) {
        return appointmentRepository.findAllByDateAndDoctorId(date, id);
    }

    @Override
    public Page<Appointment> findAllByDoctorId(Long id, Pageable pageable) {
        return appointmentRepository.findAllByDoctorId(id, pageable);
    }

    @Override
    public List<Appointment> findAllByUserId(Long id) {
        return appointmentRepository.findAllByUserId(id);
    }

    @Override
    public List<Appointment> findAllByDoctorId(Long id) {
        return appointmentRepository.findAllByDoctorId(id);
    }

    @Override
    public Appointment get(Long id) {
        return appointmentRepository.getOne(id);
    }


    @Override
    public void save(Appointment app) {
        appointmentRepository.save(app);
    }

    @Override
    public void delete(Long id) {
        appointmentRepository.deleteById(id);
    }
}
