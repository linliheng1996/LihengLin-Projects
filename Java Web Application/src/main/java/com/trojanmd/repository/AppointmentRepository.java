package com.trojanmd.repository;

import com.trojanmd.domain.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByDateAndDoctorIdAndIsAvailable(Date date, Long id, Boolean isAvailable);
    List<Appointment> findAllByDateAndDoctorId(Date date, Long id);
    Page<Appointment> findAllByDoctorId(Long id, Pageable pageable);
    List<Appointment> findAllByUserId(Long id);
    List<Appointment> findAllByDoctorId(Long id);

}
