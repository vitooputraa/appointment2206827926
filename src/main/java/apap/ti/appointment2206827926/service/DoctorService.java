package apap.ti.appointment2206827926.service;

import apap.ti.appointment2206827926.dto.DoctorRequestDTO;
import apap.ti.appointment2206827926.model.Doctor;

import java.util.List;

public interface DoctorService {
    List<Doctor> getAllDoctors();

    Doctor getDoctorById(String id);

    Doctor createDoctor(DoctorRequestDTO dto);

    Doctor updateDoctor(DoctorRequestDTO dto);

    Doctor softDeleteDoctor(String id);
}