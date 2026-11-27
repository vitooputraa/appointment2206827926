package apap.ti.appointment2206827926.service;

import apap.ti.appointment2206827926.model.Patient;

import java.util.Optional;

public interface PatientService {
    Optional<Patient> findByNik(String nik);

    Patient getByNik(String nik);

    Patient savePatient(Patient patient);
}