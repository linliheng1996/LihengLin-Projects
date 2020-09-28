package com.trojanmd.service;

import com.trojanmd.domain.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface AppointmentService {
    List<Appointment> findAllByDateAndDoctorIdAndIsAvailable(Date date, Long id, Boolean isAvailable);
    List<Appointment> findAllByDateAndDoctorId(Date date, Long id);
    Page<Appointment> findAllByDoctorId(Long id, Pageable pageable);
    List<Appointment> findAllByUserId(Long id);
    List<Appointment> findAllByDoctorId(Long id);
    Appointment get(Long id);
//    void updateUserId(Long id, User user);
//    void updateDoctorId(Long id, Doctor doctor);
    void save(Appointment app);
    void delete(Long id);
}
