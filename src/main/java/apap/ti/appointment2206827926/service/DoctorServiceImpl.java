package apap.ti.appointment2206827926.service;

import apap.ti.appointment2206827926.dto.DoctorRequestDTO;
import apap.ti.appointment2206827926.model.Doctor;
import apap.ti.appointment2206827926.repository.DoctorRepository;
import apap.ti.appointment2206827926.util.DoctorHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findByDeletedAtIsNull();
    }

    @Override
    public Doctor getDoctorById(String id) {
        return doctorRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor dengan ID " + id + " tidak ditemukan."));
    }

    @Override
    @Transactional
    public Doctor createDoctor(DoctorRequestDTO dto) {
        String id = generateDoctorId(dto.getSpecialist());
        List<Integer> cleanSchedules = sanitizeSchedules(dto.getSchedules());
        String formattedName = DoctorHelper.formatDoctorName(dto.getName(), dto.getSpecialist());

        Doctor doctor = Doctor.builder()
                .id(id)
                .name(formattedName)
                .specialist(dto.getSpecialist())
                .email(dto.getEmail().trim())
                .gender(Boolean.TRUE.equals(dto.getGender()))
                .yearsOfExperience(dto.getYearsOfExperience() != null ? dto.getYearsOfExperience() : 0)
                .schedules(cleanSchedules)
                .fee(dto.getFee() != null ? dto.getFee() : 0L)
                .build();

        return doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public Doctor updateDoctor(DoctorRequestDTO dto) {
        Doctor doctor = getDoctorById(dto.getId());
        List<Integer> cleanSchedules = sanitizeSchedules(dto.getSchedules());
        String formattedName = DoctorHelper.formatDoctorName(dto.getName(), dto.getSpecialist());

        doctor.setName(formattedName);
        doctor.setEmail(dto.getEmail().trim());
        doctor.setGender(Boolean.TRUE.equals(dto.getGender()));
        doctor.setSpecialist(dto.getSpecialist());
        doctor.setYearsOfExperience(dto.getYearsOfExperience() != null ? dto.getYearsOfExperience() : 0);
        doctor.setSchedules(cleanSchedules);
        doctor.setFee(dto.getFee() != null ? dto.getFee() : 0L);

        return doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public Doctor softDeleteDoctor(String id) {
        Doctor doctor = getDoctorById(id);
        doctor.setDeletedAt(LocalDateTime.now());
        return doctorRepository.save(doctor);
    }

    private String generateDoctorId(int specialist) {
        String prefix = DoctorHelper.getSpecializationCode(specialist);
        long sequence = doctorRepository.countByIdStartingWith(prefix) + 1;
        String candidateId = String.format("%s%03d", prefix, sequence);

        while (doctorRepository.existsById(candidateId)) {
            sequence++;
            candidateId = String.format("%s%03d", prefix, sequence);
        }
        return candidateId;
    }

    private List<Integer> sanitizeSchedules(List<Integer> schedules) {
        if (schedules == null) {
            return new ArrayList<>();
        }
        return schedules.stream()
                .filter(day -> day != null && day >= 1 && day <= 7)
                .distinct()
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
    }
}