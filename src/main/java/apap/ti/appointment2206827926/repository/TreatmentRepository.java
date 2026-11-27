package apap.ti.appointment2206827926.repository;

import apap.ti.appointment2206827926.model.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
}