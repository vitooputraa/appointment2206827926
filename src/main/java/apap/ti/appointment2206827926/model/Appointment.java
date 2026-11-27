package apap.ti.appointment2206827926.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "appointment")
public class Appointment {

    // 10 karakter: [1..3] kode spesialisasi, [4..7] DDMM, [8..10] urutan hari itu
    // (contoh: BDH1909001)
    @Id
    @Column(name = "id", length = 10, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "appointment_treatment", joinColumns = @JoinColumn(name = "appointment_id"), inverseJoinColumns = @JoinColumn(name = "treatment_id"))
    private List<Treatment> treatments = new ArrayList<>();

    @Column(name = "total_fee", nullable = false)
    private long totalFee;

    // 0: Created, 1: Done, 2: Cancelled
    @Column(name = "status", nullable = false)
    private int status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}