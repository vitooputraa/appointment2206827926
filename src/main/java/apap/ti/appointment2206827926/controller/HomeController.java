package apap.ti.appointment2206827926.controller;

import apap.ti.appointment2206827926.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final AppointmentRepository appointmentRepository;

    @GetMapping("/")
    public String home(Model model) {
        long todayOngoingCount = appointmentRepository.countByDateAndDeletedAtIsNull(LocalDate.now());
        model.addAttribute("todayOngoingCount", todayOngoingCount);
        model.addAttribute("activePage", "home");
        return "home";
    }
}