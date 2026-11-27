package apap.ti.appointment2206827926.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAppointmentDTO {

    private String nik;
    private String doctorId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String createdBy;
}