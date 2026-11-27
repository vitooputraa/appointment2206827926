package apap.ti.appointment2206827926.service;

import apap.ti.appointment2206827926.dto.CreateAppointmentDTO;
import apap.ti.appointment2206827926.dto.CreateAppointmentWithPatientDTO;
import apap.ti.appointment2206827926.dto.UpdateAppointmentDTO;
import apap.ti.appointment2206827926.dto.UpdateAppointmentNoteDTO;
import apap.ti.appointment2206827926.model.Appointment;
import apap.ti.appointment2206827926.model.Treatment;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {
    List<Appointment> getAllAppointments();

    List<Appointment> getAppointmentsByDateRange(LocalDate fromDate, LocalDate toDate);

    Appointment getAppointmentById(String id);

    List<Treatment> getAllTreatments();

    List<LocalDate> getAvailableSchedules(String doctorId, String excludeAppointmentId);

    Appointment createAppointmentForRegisteredPatient(CreateAppointmentDTO dto);

    Appointment createAppointmentWithNewPatient(CreateAppointmentWithPatientDTO dto);

    boolean canUpdateAppointment(Appointment appointment);

    Appointment updateAppointment(UpdateAppointmentDTO dto);

    Appointment updateAppointmentNote(UpdateAppointmentNoteDTO dto);

    Appointment markAsDone(String id);

    Appointment cancelAppointment(String id);

    Appointment softDeleteAppointment(String id);

    List<Integer> getAppointmentChartData(String period, int year);
}