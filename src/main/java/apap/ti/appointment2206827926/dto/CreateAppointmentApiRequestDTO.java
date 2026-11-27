package apap.ti.appointment2206827926.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAppointmentApiRequestDTO {
    private String doctorId;
    private LocalDate date;
}