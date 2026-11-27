package apap.ti.appointment2206827926.service;

import apap.ti.appointment2206827926.model.Appointment;
import apap.ti.appointment2206827926.model.Doctor;
import apap.ti.appointment2206827926.model.Patient;
import apap.ti.appointment2206827926.repository.AppointmentRepository;
import apap.ti.appointment2206827926.repository.DoctorRepository;
import apap.ti.appointment2206827926.repository.PatientRepository;
import apap.ti.appointment2206827926.repository.TreatmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private TreatmentRepository treatmentRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Appointment appt1;
    private Appointment appt2;
    private Appointment appt3;

    @BeforeEach
    void setUp() {
        Doctor doctor = Doctor.builder()
                .id("BDH001")
                .name("dr. Budi Sasongko, Sp.B")
                .specialist(3)
                .fee(300000L)
                .build();

        Patient patient = Patient.builder()
                .id(UUID.randomUUID())
                .nik("3274010909600001")
                .name("Nurhasanah")
                .build();

        appt1 = Appointment.builder()
                .id("BDH1010001")
                .doctor(doctor)
                .patient(patient)
                .date(LocalDate.of(2026, 10, 10))
                .totalFee(300000L)
                .status(0)
                .build();

        appt2 = Appointment.builder()
                .id("BDH1510002")
                .doctor(doctor)
                .patient(patient)
                .date(LocalDate.of(2026, 10, 15))
                .totalFee(300000L)
                .status(1)
                .build();

        appt3 = Appointment.builder()
                .id("BDH2010003")
                .doctor(doctor)
                .patient(patient)
                .date(LocalDate.of(2026, 10, 20))
                .totalFee(300000L)
                .status(0)
                .build();
    }

    @Test
    void testGetAppointmentsByDateRange_InclusiveBounds() {
        LocalDate from = LocalDate.of(2026, 10, 10);
        LocalDate to = LocalDate.of(2026, 10, 15);

        when(appointmentRepository.findByDeletedAtIsNullAndDateBetween(from, to))
                .thenReturn(List.of(appt1, appt2));

        List<Appointment> result = appointmentService.getAppointmentsByDateRange(from, to);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(a -> !a.getDate().isBefore(from) && !a.getDate().isAfter(to)));
        verify(appointmentRepository, times(1)).findByDeletedAtIsNullAndDateBetween(from, to);
    }

    @Test
    void testGetAppointmentsByDateRange_OnlyFromDateProvided() {
        LocalDate from = LocalDate.of(2026, 10, 15);

        when(appointmentRepository.findByDeletedAtIsNull())
                .thenReturn(List.of(appt1, appt2, appt3));

        List<Appointment> result = appointmentService.getAppointmentsByDateRange(from, null);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(a -> !a.getDate().isBefore(from)));
    }

    @Test
    void testGetAppointmentsByDateRange_InvalidRangeThrowsException() {
        LocalDate from = LocalDate.of(2026, 10, 20);
        LocalDate to = LocalDate.of(2026, 10, 10);

        assertThrows(IllegalArgumentException.class,
                () -> appointmentService.getAppointmentsByDateRange(from, to));
    }
}