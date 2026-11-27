package apap.ti.appointment2206827926.service;

import apap.ti.appointment2206827926.model.Patient;
import apap.ti.appointment2206827926.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public Optional<Patient> findByNik(String nik) {
        if (nik == null || nik.isBlank()) {
            return Optional.empty();
        }
        return patientRepository.findByNik(nik.trim());
    }

    @Override
    public Patient getByNik(String nik) {
        return findByNik(nik)
                .orElseThrow(() -> new IllegalArgumentException("Pasien dengan NIK " + nik + " tidak ditemukan."));
    }

    @Override
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }
}