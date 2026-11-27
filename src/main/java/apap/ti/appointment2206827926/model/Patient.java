package apap.ti.appointment2206827926.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nik", nullable = false, unique = true)
    private String nik;

    @Column(name = "name", nullable = false)
    private String name;

    // true: Perempuan (Female), false: Laki-laki (Male)
    @Column(name = "gender", nullable = false)
    private boolean gender;

    @Column(name = "email", nullable = false)
    private String email;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "birth_place")
    private String birthPlace;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Appointment> appointments = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}