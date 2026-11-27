package apap.ti.appointment2206827926.repository;

import apap.ti.appointment2206827926.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {
    List<Doctor> findByDeletedAtIsNull();

    Optional<Doctor> findByIdAndDeletedAtIsNull(String id);

    long countByIdStartingWith(String prefix);
}