package apap.ti.appointment2206827926.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAppointmentNoteDTO {

    private String id;
    private String diagnosis;

    @Builder.Default
    private List<Long> treatmentIds = new ArrayList<>();
}