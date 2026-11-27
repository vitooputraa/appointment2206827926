package apap.ti.appointment2206827926.repository;

import apap.ti.appointment2206827926.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByNik(String nik);

    boolean existsByNik(String nik);
}