package apap.ti.appointment2206827926.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAppointmentWithPatientDTO {

    private String name;
    private String nik;
    private String email;
    private Boolean gender; // true: Female, false: Male

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private String birthPlace;
    private String doctorId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String createdBy;
}