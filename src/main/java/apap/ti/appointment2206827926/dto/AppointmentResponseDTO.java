package apap.ti.appointment2206827926.dto;

import apap.ti.appointment2206827926.model.Appointment;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponseDTO {

    private String id;
    private String doctorId;
    private String doctorName;
    private String patientNik;
    private String patientName;
    private LocalDate date;
    private String diagnosis;
    private List<TreatmentSummaryDTO> treatments;
    private long totalFee;
    private int status;
    private String statusText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TreatmentSummaryDTO {
        private long id;
        private String name;
        private long price;
    }

    public static AppointmentResponseDTO fromEntity(Appointment appointment) {
        String statusLabel = switch (appointment.getStatus()) {
            case 1 -> "Done";
            case 2 -> "Cancelled";
            default -> "Created";
        };

        List<TreatmentSummaryDTO> treatmentList = appointment.getTreatments() == null
                ? List.of()
                : appointment.getTreatments().stream()
                        .map(t -> TreatmentSummaryDTO.builder()
                                .id(t.getId())
                                .name(t.getName())
                                .price(t.getPrice())
                                .build())
                        .toList();

        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getName())
                .patientNik(appointment.getPatient().getNik())
                .patientName(appointment.getPatient().getName())
                .date(appointment.getDate())
                .diagnosis(appointment.getDiagnosis())
                .treatments(treatmentList)
                .totalFee(appointment.getTotalFee())
                .status(appointment.getStatus())
                .statusText(statusLabel)
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}