package apap.ti.appointment2206827926.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorRequestDTO {

    private String id;
    private String name;
    private Integer specialist;
    private String email;
    private Boolean gender; // true: Female, false: Male
    private Integer yearsOfExperience;

    @Builder.Default
    private List<Integer> schedules = new ArrayList<>();

    private Long fee;
}