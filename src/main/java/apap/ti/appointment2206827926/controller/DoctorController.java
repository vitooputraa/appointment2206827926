package apap.ti.appointment2206827926.controller;

import apap.ti.appointment2206827926.dto.DoctorRequestDTO;
import apap.ti.appointment2206827926.model.Appointment;
import apap.ti.appointment2206827926.model.Doctor;
import apap.ti.appointment2206827926.service.DoctorService;
import apap.ti.appointment2206827926.util.DoctorHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/doctor/all")
    public String viewAllDoctors(Model model) {
        List<Doctor> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);
        model.addAttribute("specializations", DoctorHelper.getAllSpecializations());
        model.addAttribute("days", DoctorHelper.getAllDays());
        model.addAttribute("helper", new DoctorHelper());
        model.addAttribute("activePage", "doctors");
        return "doctor/viewall-doctor";
    }

    @GetMapping("/doctor/{id}")
    public String detailDoctor(@PathVariable("id") String id, Model model) {
        Doctor doctor = doctorService.getDoctorById(id);
        List<Appointment> activeAppointments = doctor.getAppointments() == null
                ? List.of()
                : doctor.getAppointments().stream()
                        .filter(a -> a.getDeletedAt() == null)
                        .toList();

        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", activeAppointments);
        model.addAttribute("specializationName", DoctorHelper.getSpecializationName(doctor.getSpecialist()));
        model.addAttribute("formattedSchedules", DoctorHelper.formatSchedules(doctor.getSchedules()));
        model.addAttribute("activePage", "doctors");
        return "doctor/detail-doctor";
    }

    @GetMapping("/doctor/create")
    public String formCreateDoctor(Model model) {
        DoctorRequestDTO dto = new DoctorRequestDTO();
        dto.getSchedules().add(1); // Default 1 baris jadwal (Monday)

        model.addAttribute("doctorDTO", dto);
        model.addAttribute("specializations", DoctorHelper.getAllSpecializations());
        model.addAttribute("days", DoctorHelper.getAllDays());
        model.addAttribute("activePage", "doctors");
        return "doctor/form-create-doctor";
    }

    @PostMapping("/doctor/create")
    public String submitCreateDoctor(@ModelAttribute DoctorRequestDTO doctorDTO, Model model) {
        Doctor created = doctorService.createDoctor(doctorDTO);
        model.addAttribute("message", "Berhasil menambahkan Dokter " + created.getName() + ".");
        model.addAttribute("backUrl", "/doctor/all");
        model.addAttribute("activePage", "doctors");
        return "response";
    }

    @GetMapping("/doctor/{id}/update")
    public String formUpdateDoctor(@PathVariable("id") String id, Model model) {
        Doctor doctor = doctorService.getDoctorById(id);
        List<Integer> schedules = new ArrayList<>(doctor.getSchedules());
        if (schedules.isEmpty()) {
            schedules.add(1);
        }

        DoctorRequestDTO dto = DoctorRequestDTO.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .specialist(doctor.getSpecialist())
                .email(doctor.getEmail())
                .gender(doctor.isGender())
                .yearsOfExperience(doctor.getYearsOfExperience())
                .schedules(schedules)
                .fee(doctor.getFee())
                .build();

        model.addAttribute("doctorDTO", dto);
        model.addAttribute("specializations", DoctorHelper.getAllSpecializations());
        model.addAttribute("days", DoctorHelper.getAllDays());
        model.addAttribute("activePage", "doctors");
        return "doctor/form-update-doctor";
    }

    @PostMapping("/doctor/update")
    public String submitUpdateDoctor(@ModelAttribute DoctorRequestDTO doctorDTO, Model model) {
        Doctor updated = doctorService.updateDoctor(doctorDTO);
        model.addAttribute("message", "Berhasil mengupdate Dokter " + updated.getName() + ".");
        model.addAttribute("backUrl", "/doctor/all");
        model.addAttribute("activePage", "doctors");
        return "response";
    }

    @GetMapping("/doctor/{id}/delete")
    public String confirmDeleteDoctor(@PathVariable("id") String id, Model model) {
        Doctor doctor = doctorService.getDoctorById(id);
        model.addAttribute("doctor", doctor);
        model.addAttribute("specializationName", DoctorHelper.getSpecializationName(doctor.getSpecialist()));
        model.addAttribute("formattedSchedules", DoctorHelper.formatSchedules(doctor.getSchedules()));
        model.addAttribute("activePage", "doctors");
        return "doctor/delete-doctor";
    }

    @PostMapping("/doctor/delete")
    public String submitDeleteDoctor(@RequestParam("id") String id, Model model) {
        Doctor deleted = doctorService.softDeleteDoctor(id);
        model.addAttribute("message", "Berhasil menghapus Dokter " + deleted.getName() + ".");
        model.addAttribute("backUrl", "/doctor/all");
        model.addAttribute("activePage", "doctors");
        return "response";
    }
}