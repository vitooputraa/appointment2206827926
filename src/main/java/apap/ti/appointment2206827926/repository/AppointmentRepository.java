package apap.ti.appointment2206827926.repository;

import apap.ti.appointment2206827926.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {
    List<Appointment> findByDeletedAtIsNull();

    Optional<Appointment> findByIdAndDeletedAtIsNull(String id);

    List<Appointment> findByDeletedAtIsNullAndDateBetween(LocalDate startDate, LocalDate endDate);

    long countByDateAndDeletedAtIsNull(LocalDate date);

    long countByIdStartingWith(String prefix);

    boolean existsByDoctorIdAndDateAndDeletedAtIsNull(String doctorId, LocalDate date);

    boolean existsByDoctorIdAndDateAndDeletedAtIsNullAndIdNot(String doctorId, LocalDate date, String id);
}