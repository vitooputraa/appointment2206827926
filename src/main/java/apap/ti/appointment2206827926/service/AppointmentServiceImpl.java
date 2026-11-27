package apap.ti.appointment2206827926.service;

import apap.ti.appointment2206827926.dto.CreateAppointmentDTO;
import apap.ti.appointment2206827926.dto.CreateAppointmentWithPatientDTO;
import apap.ti.appointment2206827926.dto.UpdateAppointmentDTO;
import apap.ti.appointment2206827926.dto.UpdateAppointmentNoteDTO;
import apap.ti.appointment2206827926.model.Appointment;
import apap.ti.appointment2206827926.model.Doctor;
import apap.ti.appointment2206827926.model.Patient;
import apap.ti.appointment2206827926.model.Treatment;
import apap.ti.appointment2206827926.repository.AppointmentRepository;
import apap.ti.appointment2206827926.repository.DoctorRepository;
import apap.ti.appointment2206827926.repository.PatientRepository;
import apap.ti.appointment2206827926.repository.TreatmentRepository;
import apap.ti.appointment2206827926.util.DoctorHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final TreatmentRepository treatmentRepository;

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findByDeletedAtIsNull();
    }

    @Override
    public List<Appointment> getAppointmentsByDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("Tanggal 'From' tidak boleh lebih akhir dari tanggal 'To'.");
        }

        if (fromDate != null && toDate != null) {
            return appointmentRepository.findByDeletedAtIsNullAndDateBetween(fromDate, toDate);
        }

        return appointmentRepository.findByDeletedAtIsNull().stream()
                .filter(appt -> (fromDate == null || !appt.getDate().isBefore(fromDate))
                        && (toDate == null || !appt.getDate().isAfter(toDate)))
                .toList();
    }

    @Override
    public Appointment getAppointmentById(String id) {
        return appointmentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment dengan ID " + id + " tidak ditemukan."));
    }

    @Override
    public List<Treatment> getAllTreatments() {
        return treatmentRepository.findAll();
    }

    @Override
    public List<LocalDate> getAvailableSchedules(String doctorId, String excludeAppointmentId) {
        Doctor doctor = doctorRepository.findByIdAndDeletedAtIsNull(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Dokter dengan ID " + doctorId + " tidak ditemukan."));

        LocalDate today = LocalDate.now();
        List<LocalDate> availableDates = new ArrayList<>();

        // 4 pekan ke depan (28 hari mulai hari ini) sesuai hari praktik dokter
        for (int i = 0; i < 28; i++) {
            LocalDate candidate = today.plusDays(i);
            int dayOfWeek = candidate.getDayOfWeek().getValue();

            if (doctor.getSchedules() != null && doctor.getSchedules().contains(dayOfWeek)) {
                boolean isBooked = (excludeAppointmentId == null)
                        ? appointmentRepository.existsByDoctorIdAndDateAndDeletedAtIsNull(doctorId, candidate)
                        : appointmentRepository.existsByDoctorIdAndDateAndDeletedAtIsNullAndIdNot(
                                doctorId, candidate, excludeAppointmentId);

                if (!isBooked) {
                    availableDates.add(candidate);
                }
            }
        }
        return availableDates;
    }

    @Override
    @Transactional
    public Appointment createAppointmentForRegisteredPatient(CreateAppointmentDTO dto) {
        Patient patient = patientRepository.findByNik(dto.getNik().trim())
                .orElseThrow(
                        () -> new IllegalArgumentException("Pasien dengan NIK " + dto.getNik() + " tidak ditemukan."));

        return buildAndSaveAppointment(patient, dto.getDoctorId(), dto.getDate());
    }

    @Override
    @Transactional
    public Appointment createAppointmentWithNewPatient(CreateAppointmentWithPatientDTO dto) {
        String cleanNik = dto.getNik().trim();
        if (patientRepository.existsByNik(cleanNik)) {
            throw new IllegalArgumentException("Pasien dengan NIK " + cleanNik + " sudah terdaftar.");
        }

        Patient newPatient = Patient.builder()
                .nik(cleanNik)
                .name(dto.getName().trim())
                .email(dto.getEmail().trim())
                .gender(Boolean.TRUE.equals(dto.getGender()))
                .birthDate(dto.getBirthDate())
                .birthPlace(dto.getBirthPlace() != null ? dto.getBirthPlace().trim() : "-")
                .build();

        Patient savedPatient = patientRepository.save(newPatient);
        return buildAndSaveAppointment(savedPatient, dto.getDoctorId(), dto.getDate());
    }

    private Appointment buildAndSaveAppointment(Patient patient, String doctorId, LocalDate appointmentDate) {
        Doctor doctor = doctorRepository.findByIdAndDeletedAtIsNull(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Dokter dengan ID " + doctorId + " tidak ditemukan."));

        if (appointmentDate == null) {
            throw new IllegalArgumentException("Tanggal appointment wajib diisi.");
        }

        int dayOfWeek = appointmentDate.getDayOfWeek().getValue();
        if (doctor.getSchedules() == null || !doctor.getSchedules().contains(dayOfWeek)) {
            throw new IllegalArgumentException("Dokter tidak praktik pada hari yang dipilih.");
        }

        if (appointmentRepository.existsByDoctorIdAndDateAndDeletedAtIsNull(doctorId, appointmentDate)) {
            throw new IllegalArgumentException("Dokter sudah memiliki appointment pada tanggal tersebut.");
        }

        String generatedId = generateAppointmentId(doctor.getSpecialist(), appointmentDate);

        Appointment appointment = Appointment.builder()
                .id(generatedId)
                .doctor(doctor)
                .patient(patient)
                .date(appointmentDate)
                .totalFee(doctor.getFee())
                .status(0) // 0: Created
                .build();

        return appointmentRepository.save(appointment);
    }

    private String generateAppointmentId(int specialist, LocalDate date) {
        String specCode = DoctorHelper.getSpecializationCode(specialist);
        String datePart = date.format(DateTimeFormatter.ofPattern("ddMM"));
        long sequence = appointmentRepository.countByDateAndDeletedAtIsNull(date) + 1;

        String candidateId = String.format("%s%s%03d", specCode, datePart, sequence);
        while (appointmentRepository.existsById(candidateId)) {
            sequence++;
            candidateId = String.format("%s%s%03d", specCode, datePart, sequence);
        }
        return candidateId;
    }

    @Override
    public boolean canUpdateAppointment(Appointment appointment) {
        if (appointment == null || appointment.getStatus() != 0) {
            return false;
        }
        // Tidak dapat diubah jika waktu sekarang adalah 1 hari (atau kurang) sebelum
        // tanggal appointment
        return LocalDate.now().isBefore(appointment.getDate().minusDays(1));
    }

    @Override
    @Transactional
    public Appointment updateAppointment(UpdateAppointmentDTO dto) {
        Appointment appointment = getAppointmentById(dto.getId());

        if (!canUpdateAppointment(appointment)) {
            throw new IllegalArgumentException(
                    "Appointment tidak dapat diubah karena sudah H-1 atau kurang sebelum tanggal appointment.");
        }

        Doctor newDoctor = doctorRepository.findByIdAndDeletedAtIsNull(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Dokter dengan ID " + dto.getDoctorId() + " tidak ditemukan."));

        LocalDate newDate = dto.getDate();
        if (newDate == null) {
            throw new IllegalArgumentException("Tanggal appointment wajib diisi.");
        }

        int dayOfWeek = newDate.getDayOfWeek().getValue();
        if (newDoctor.getSchedules() == null || !newDoctor.getSchedules().contains(dayOfWeek)) {
            throw new IllegalArgumentException("Dokter tidak praktik pada hari yang dipilih.");
        }

        if (appointmentRepository.existsByDoctorIdAndDateAndDeletedAtIsNullAndIdNot(
                newDoctor.getId(), newDate, appointment.getId())) {
            throw new IllegalArgumentException("Dokter sudah memiliki appointment pada tanggal tersebut.");
        }

        long existingTreatmentsFee = appointment.getTreatments() == null
                ? 0L
                : appointment.getTreatments().stream().mapToLong(Treatment::getPrice).sum();

        appointment.setDoctor(newDoctor);
        appointment.setDate(newDate);
        appointment.setTotalFee(newDoctor.getFee() + existingTreatmentsFee);

        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public Appointment updateAppointmentNote(UpdateAppointmentNoteDTO dto) {
        Appointment appointment = getAppointmentById(dto.getId());

        List<Treatment> selectedTreatments = new ArrayList<>();
        if (dto.getTreatmentIds() != null) {
            for (Long treatmentId : dto.getTreatmentIds()) {
                if (treatmentId != null) {
                    treatmentRepository.findById(treatmentId).ifPresent(selectedTreatments::add);
                }
            }
        }

        long treatmentsTotal = selectedTreatments.stream()
                .mapToLong(Treatment::getPrice)
                .sum();

        appointment.setDiagnosis(dto.getDiagnosis());
        appointment.setTreatments(selectedTreatments);
        appointment.setTotalFee(appointment.getDoctor().getFee() + treatmentsTotal);

        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public Appointment markAsDone(String id) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(1);
        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public Appointment cancelAppointment(String id) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(2);
        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public Appointment softDeleteAppointment(String id) {
        Appointment appointment = getAppointmentById(id);
        appointment.setDeletedAt(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Integer> getAppointmentChartData(String period, int year) {
        List<Appointment> yearlyAppointments = appointmentRepository.findByDeletedAtIsNull().stream()
                .filter(appt -> appt.getDate() != null && appt.getDate().getYear() == year)
                .toList();

        if ("quarter".equalsIgnoreCase(period)) {
            int[] quarters = new int[4];
            for (Appointment appt : yearlyAppointments) {
                int quarterIdx = (appt.getDate().getMonthValue() - 1) / 3;
                quarters[quarterIdx]++;
            }
            return List.of(quarters[0], quarters[1], quarters[2], quarters[3]);
        }

        int[] months = new int[12];
        for (Appointment appt : yearlyAppointments) {
            int monthIdx = appt.getDate().getMonthValue() - 1;
            months[monthIdx]++;
        }
        List<Integer> result = new ArrayList<>(12);
        for (int count : months) {
            result.add(count);
        }
        return result;
    }
}