package apap.ti.appointment2206827926.restcontroller;

import apap.ti.appointment2206827926.dto.AppointmentResponseDTO;
import apap.ti.appointment2206827926.dto.CreateAppointmentApiRequestDTO;
import apap.ti.appointment2206827926.dto.CreateAppointmentDTO;
import apap.ti.appointment2206827926.model.Appointment;
import apap.ti.appointment2206827926.service.AppointmentService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppointmentRestController {

    private final AppointmentService appointmentService;

    @Getter
    @AllArgsConstructor
    public static class ScheduleOption {
        private String value;
        private String label;
    }

    // Fitur 2: GET /api/appointment/all
    @GetMapping("/appointment/all")
    public ResponseEntity<?> getAllAppointmentsApi(
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByDateRange(from, to);
            List<AppointmentResponseDTO> data = appointments.stream()
                    .map(AppointmentResponseDTO::fromEntity)
                    .toList();

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Berhasil mengambil data appointment.");
            response.put("count", data.size());
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", HttpStatus.BAD_REQUEST.value());
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Helper Dropdown Schedule 4 pekan ke depan
    @GetMapping("/doctor/{doctorId}/schedules")
    public ResponseEntity<?> getDoctorAvailableSchedules(
            @PathVariable("doctorId") String doctorId,
            @RequestParam(value = "excludeAppointmentId", required = false) String excludeAppointmentId) {
        try {
            List<LocalDate> dates = appointmentService.getAvailableSchedules(doctorId, excludeAppointmentId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);

            List<ScheduleOption> options = dates.stream().map(date -> {
                String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                return new ScheduleOption(date.toString(), dayName + ", " + date.format(formatter));
            }).toList();

            return ResponseEntity.ok(options);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Fitur 14: GET /api/appointment/stat?period={period}&year={year}
    @GetMapping("/appointment/stat")
    public ResponseEntity<?> getAppointmentStatisticApi(
            @RequestParam(value = "period", defaultValue = "monthly") String period,
            @RequestParam(value = "year", required = false) Integer year) {
        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        List<Integer> counts = appointmentService.getAppointmentChartData(period, targetYear);

        List<String> labels = "quarter".equalsIgnoreCase(period)
                ? List.of("Q1", "Q2", "Q3", "Q4")
                : List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("period", period);
        response.put("year", targetYear);
        response.put("labels", labels);
        response.put("data", counts);

        return ResponseEntity.ok(response);
    }

    // Fitur Bonus 17: GET /api/appointment/detail?id={id}
    @GetMapping("/appointment/detail")
    public ResponseEntity<?> getAppointmentDetailApi(@RequestParam("id") String id) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            AppointmentResponseDTO dto = AppointmentResponseDTO.fromEntity(appointment);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Berhasil mengambil detail appointment.");
            response.put("data", dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", HttpStatus.NOT_FOUND.value());
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // Fitur Bonus 18: POST /api/appointment/create?nik={nik}
    @PostMapping("/appointment/create")
    public ResponseEntity<?> createAppointmentApi(
            @RequestParam("nik") String nik,
            @RequestBody CreateAppointmentApiRequestDTO request) {
        try {
            CreateAppointmentDTO dto = CreateAppointmentDTO.builder()
                    .nik(nik)
                    .doctorId(request.getDoctorId())
                    .date(request.getDate())
                    .build();

            Appointment created = appointmentService.createAppointmentForRegisteredPatient(dto);
            AppointmentResponseDTO responseDTO = AppointmentResponseDTO.fromEntity(created);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", HttpStatus.CREATED.value());
            response.put("message", "Berhasil membuat appointment " + created.getId() + ".");
            response.put("data", responseDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", HttpStatus.BAD_REQUEST.value());
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}