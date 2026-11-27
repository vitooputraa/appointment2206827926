package apap.ti.appointment2206827926.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "doctor")
public class Doctor {

    // 6 karakter: [1..3] kode spesialisasi, [4..6] nomor urut unik (contoh: BDH001)
    @Id
    @Column(name = "id", length = 6, nullable = false, updatable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    // 0..16 sesuai daftar spesialisasi dokter
    @Column(name = "specialist", nullable = false)
    private int specialist;

    @Column(name = "email", nullable = false)
    private String email;

    // true: Perempuan (Female), false: Laki-laki (Male)
    @Column(name = "gender", nullable = false)
    private boolean gender;

    @Column(name = "years_of_experience", nullable = false)
    private int yearsOfExperience;

    // 1 = Monday .. 7 = Sunday (DayOfWeek)
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doctor_schedules", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "day_of_week")
    private List<Integer> schedules = new ArrayList<>();

    @Column(name = "fee", nullable = false)
    private long fee;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}