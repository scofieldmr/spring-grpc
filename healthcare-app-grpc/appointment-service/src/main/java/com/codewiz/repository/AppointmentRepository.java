package com.codewiz.repository;

import com.codewiz.model.Appointment;
import org.springframework.data.repository.CrudRepository;

public interface AppointmentRepository extends CrudRepository<Appointment, Long> {
}
