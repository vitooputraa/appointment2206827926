package apap.ti.appointment2206827926.controller;

import apap.ti.appointment2206827926.dto.CreateAppointmentDTO;
import apap.ti.appointment2206827926.dto.CreateAppointmentWithPatientDTO;
import apap.ti.appointment2206827926.dto.UpdateAppointmentDTO;
import apap.ti.appointment2206827926.dto.UpdateAppointmentNoteDTO;
import apap.ti.appointment2206827926.model.Appointment;
import apap.ti.appointment2206827926.model.Patient;
import apap.ti.appointment2206827926.model.Treatment;
import apap.ti.appointment2206827926.restcontroller.AppointmentRestController.ScheduleOption;
import apap.ti.appointment2206827926.service.AppointmentService;
import apap.ti.appointment2206827926.service.DoctorService;
import apap.ti.appointment2206827926.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    // Fitur 2: Read All Appointment (tabel diisi dari GET /api/appointment/all)
    @GetMapping("/appointment/all")
    public String viewAllAppointments(Model model) {
        model.addAttribute("activePage", "appointments");
        return "appointment/viewall-appointment";
    }

    // Fitur 3: Read Detail Appointment
    @GetMapping("/appointment/{id}")
    public String detailAppointment(@PathVariable("id") String id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        boolean canUpdate = appointmentService.canUpdateAppointment(appointment);

        model.addAttribute("appointment", appointment);
        model.addAttribute("canUpdate", canUpdate);
        model.addAttribute("activePage", "appointments");
        return "appointment/detail-appointment";
    }

    // Fitur 4: Search Patient (GET /patient/search)
    @GetMapping("/patient/search")
    public String searchPatientForm(Model model) {
        model.addAttribute("activePage", "appointments");
        return "appointment/search-patient";
    }

    // Submit form search patient via POST /patient/search
    @PostMapping("/patient/search")
    public String searchPatientSubmit(@RequestParam("nik") String nik) {
        return "redirect:/patient/search/" + nik.trim();
    }

    // Fitur 4: GET & POST /patient/search/{nik}
    @RequestMapping(value = "/patient/search/{nik}", method = { RequestMethod.GET, RequestMethod.POST })
    public String searchPatientResult(@PathVariable("nik") String nik, Model model) {
        Optional<Patient> patientOpt = patientService.findByNik(nik);
        model.addAttribute("searchedNik", nik.trim());
        model.addAttribute("activePage", "appointments");

        if (patientOpt.isPresent()) {
            model.addAttribute("patient", patientOpt.get());
            return "appointment/patient-found";
        } else {
            return "appointment/patient-not-found";
        }
    }

    // Fitur 4 Flow 1: Form Create Appointment for Registered Patient
    @GetMapping("/appointment/{nik}/create")
    public String formCreateAppointmentRegistered(@PathVariable("nik") String nik, Model model) {
        Patient patient = patientService.getByNik(nik);
        CreateAppointmentDTO dto = CreateAppointmentDTO.builder()
                .nik(patient.getNik())
                .build();

        model.addAttribute("patient", patient);
        model.addAttribute("appointmentDTO", dto);
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("activePage", "appointments");
        return "appointment/form-create-appointment";
    }

    // Fitur 4 Flow 1: Submit Create Appointment for Registered Patient
    @PostMapping("/appointment/{nik}/create")
    public String submitCreateAppointmentRegistered(
            @PathVariable("nik") String nik,
            @ModelAttribute CreateAppointmentDTO appointmentDTO,
            Model model) {
        appointmentDTO.setNik(nik);
        try {
            Appointment created = appointmentService.createAppointmentForRegisteredPatient(appointmentDTO);
            model.addAttribute("message", "Berhasil membuat appointment " + created.getId() + ".");
            model.addAttribute("backUrl", "/appointment/all");
            model.addAttribute("activePage", "appointments");
            return "response";
        } catch (IllegalArgumentException e) {
            Patient patient = patientService.getByNik(nik);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("patient", patient);
            model.addAttribute("appointmentDTO", appointmentDTO);
            model.addAttribute("doctors", doctorService.getAllDoctors());
            model.addAttribute("activePage", "appointments");
            return "appointment/form-create-appointment";
        }
    }

    // Fitur 4 Flow 2: Form Create Appointment for New Patient
    @GetMapping("/appointment/create-with-patient")
    public String formCreateAppointmentWithPatient(
            @RequestParam(value = "nik", required = false) String nik,
            Model model) {
        CreateAppointmentWithPatientDTO dto = CreateAppointmentWithPatientDTO.builder()
                .nik(nik != null ? nik.trim() : "")
                .gender(false)
                .build();

        model.addAttribute("appointmentDTO", dto);
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("activePage", "appointments");
        return "appointment/form-create-with-patient";
    }

    // Fitur 4 Flow 2: Submit Create Appointment for New Patient (Transactional)
    @PostMapping("/appointment/create-with-patient")
    public String submitCreateAppointmentWithPatient(
            @ModelAttribute CreateAppointmentWithPatientDTO appointmentDTO,
            Model model) {
        try {
            Appointment created = appointmentService.createAppointmentWithNewPatient(appointmentDTO);
            model.addAttribute("message", "Berhasil membuat appointment " + created.getId() + ".");
            model.addAttribute("backUrl", "/appointment/all");
            model.addAttribute("activePage", "appointments");
            return "response";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("appointmentDTO", appointmentDTO);
            model.addAttribute("doctors", doctorService.getAllDoctors());
            model.addAttribute("activePage", "appointments");
            return "appointment/form-create-with-patient";
        }
    }

    // Fitur 5: Form Update Appointment
    @GetMapping("/appointment/{id}/update")
    public String formUpdateAppointment(@PathVariable("id") String id, Model model,
            RedirectAttributes redirectAttributes) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (!appointmentService.canUpdateAppointment(appointment)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Appointment tidak dapat diubah karena sudah H-1 atau kurang sebelum tanggal appointment.");
            return "redirect:/appointment/" + id;
        }

        UpdateAppointmentDTO dto = UpdateAppointmentDTO.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .date(appointment.getDate())
                .build();

        model.addAttribute("appointmentDTO", dto);
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("scheduleOptions",
                buildScheduleOptions(appointment.getDoctor().getId(), appointment.getId(), appointment.getDate()));
        model.addAttribute("activePage", "appointments");
        return "appointment/form-update-appointment";
    }

    // Fitur 5: Submit Update Appointment
    @PostMapping("/appointment/update")
    public String submitUpdateAppointment(@ModelAttribute UpdateAppointmentDTO appointmentDTO, Model model) {
        try {
            Appointment updated = appointmentService.updateAppointment(appointmentDTO);
            model.addAttribute("message", "Berhasil mengupdate appointment " + updated.getId() + ".");
            model.addAttribute("backUrl", "/appointment/all");
            model.addAttribute("activePage", "appointments");
            return "response";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("appointmentDTO", appointmentDTO);
            model.addAttribute("doctors", doctorService.getAllDoctors());
            model.addAttribute("scheduleOptions", buildScheduleOptions(
                    appointmentDTO.getDoctorId(), appointmentDTO.getId(), appointmentDTO.getDate()));
            model.addAttribute("activePage", "appointments");
            return "appointment/form-update-appointment";
        }
    }

    // Fitur 6: Form Update Appointment Diagnosis & Treatment (Note)
    @GetMapping("/appointment/{id}/note")
    public String formUpdateAppointmentNote(@PathVariable("id") String id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id);

        List<Long> existingTreatmentIds = new ArrayList<>();
        if (appointment.getTreatments() != null && !appointment.getTreatments().isEmpty()) {
            for (Treatment t : appointment.getTreatments()) {
                existingTreatmentIds.add(t.getId());
            }
        } else {
            existingTreatmentIds.add(null);
        }

        UpdateAppointmentNoteDTO noteDTO = UpdateAppointmentNoteDTO.builder()
                .id(appointment.getId())
                .diagnosis(appointment.getDiagnosis())
                .treatmentIds(existingTreatmentIds)
                .build();

        model.addAttribute("appointment", appointment);
        model.addAttribute("noteDTO", noteDTO);
        model.addAttribute("treatments", appointmentService.getAllTreatments());
        model.addAttribute("activePage", "appointments");
        return "appointment/form-note-appointment";
    }

    // Fitur 6: Submit Update Appointment Diagnosis & Treatment (Note)
    @PostMapping("/appointment/note")
    public String submitUpdateAppointmentNote(@ModelAttribute UpdateAppointmentNoteDTO noteDTO, Model model) {
        Appointment updated = appointmentService.updateAppointmentNote(noteDTO);
        model.addAttribute("message",
                "Berhasil mencatat diagnosis & treatment untuk appointment " + updated.getId() + ".");
        model.addAttribute("backUrl", "/appointment/all");
        model.addAttribute("activePage", "appointments");
        return "response";
    }

    // Fitur 7: Mark Appointment as Done
    @PostMapping("/appointment/{id}/done")
    public String markAppointmentAsDone(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        appointmentService.markAsDone(id);
        redirectAttributes.addFlashAttribute("successMessage", "Status appointment berhasil diubah menjadi Done.");
        return "redirect:/appointment/" + id;
    }

    // Fitur 7: Cancel Appointment
    @PostMapping("/appointment/{id}/cancel")
    public String cancelAppointment(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        appointmentService.cancelAppointment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Status appointment berhasil diubah menjadi Cancelled.");
        return "redirect:/appointment/" + id;
    }

    // Fitur 8: Form / Halaman Konfirmasi Soft Delete Appointment
    @GetMapping("/appointment/{id}/delete")
    public String confirmDeleteAppointment(@PathVariable("id") String id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        model.addAttribute("appointment", appointment);
        model.addAttribute("activePage", "appointments");
        return "appointment/delete-appointment";
    }

    // Fitur 8: Submit Soft Delete Appointment
    @PostMapping("/appointment/delete")
    public String submitDeleteAppointment(@RequestParam("id") String id, Model model) {
        Appointment deleted = appointmentService.softDeleteAppointment(id);
        model.addAttribute("message", "Berhasil menghapus Appointment " + deleted.getId() + ".");
        model.addAttribute("backUrl", "/appointment/all");
        model.addAttribute("activePage", "appointments");
        return "response";
    }

    private List<ScheduleOption> buildScheduleOptions(String doctorId, String excludeAppointmentId,
            LocalDate currentDate) {
        List<LocalDate> dates = new ArrayList<>(
                appointmentService.getAvailableSchedules(doctorId, excludeAppointmentId));
        if (currentDate != null && !dates.contains(currentDate)) {
            dates.add(0, currentDate);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);
        return dates.stream().map(date -> {
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            return new ScheduleOption(date.toString(), dayName + ", " + date.format(formatter));
        }).toList();
    }

    // Fitur 14: Halaman Statistik Appointment
    @GetMapping("/appointment/stat")
    public String viewAppointmentStatistic(Model model) {
        model.addAttribute("currentYear", LocalDate.now().getYear());
        model.addAttribute("activePage", "appointments");
        return "appointment/stat-appointment";
    }
}