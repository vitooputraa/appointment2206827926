package apap.ti.appointment2206827926.config;

import apap.ti.appointment2206827926.model.Doctor;
import apap.ti.appointment2206827926.model.Patient;
import apap.ti.appointment2206827926.model.Treatment;
import apap.ti.appointment2206827926.repository.DoctorRepository;
import apap.ti.appointment2206827926.repository.PatientRepository;
import apap.ti.appointment2206827926.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final TreatmentRepository treatmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Override
    public void run(String... args) {
        seedTreatments();
        seedDoctors();
        seedPatients();
    }

    private void seedTreatments() {
        if (treatmentRepository.count() > 0)
            return;

        List<Treatment> treatments = List.of(
                Treatment.builder().name("Konsultasi Medis Umum").price(100000L).build(),
                Treatment.builder().name("Pemeriksaan Darah Lengkap").price(250000L).build(),
                Treatment.builder().name("Pemeriksaan Elektrokardiogram (EKG)").price(350000L).build(),
                Treatment.builder().name("Rontgen Thorax").price(300000L).build(),
                Treatment.builder().name("USG Abdomen").price(450000L).build(),
                Treatment.builder().name("Scaling & Pembersihan Karang Gigi").price(400000L).build(),
                Treatment.builder().name("Penambalan Gigi").price(350000L).build(),
                Treatment.builder().name("Nebulizer & Terapi Pernapasan").price(200000L).build(),
                Treatment.builder().name("Perawatan Luka & Jahit Ringan").price(275000L).build(),
                Treatment.builder().name("Fisioterapi Dasar").price(320000L).build(),
                Treatment.builder().name("Pemeriksaan Tajam Penglihatan & Refraksi").price(180000L).build(),
                Treatment.builder().name("Audiometri & Pemeriksaan THT").price(290000L).build());

        treatmentRepository.saveAll(treatments);
    }

    private void seedDoctors() {
        if (doctorRepository.count() > 0)
            return;

        List<Doctor> doctors = List.of(
                Doctor.builder()
                        .id("ANK001")
                        .name("dr. Andi Saputra, Sp.A")
                        .specialist(2)
                        .email("andi.saputra@apapmedika.com")
                        .gender(false)
                        .yearsOfExperience(7)
                        .schedules(List.of(1, 2, 5)) // Monday, Tuesday, Friday
                        .fee(250000L)
                        .build(),
                Doctor.builder()
                        .id("PDL001")
                        .name("dr. Maya Lestari, Sp.PD")
                        .specialist(9)
                        .email("maya.lestari@apapmedika.com")
                        .gender(true)
                        .yearsOfExperience(10)
                        .schedules(List.of(2, 4)) // Tuesday, Thursday
                        .fee(300000L)
                        .build(),
                Doctor.builder()
                        .id("BDH001")
                        .name("dr. Budi Sasongko, Sp.B")
                        .specialist(3)
                        .email("budi.sasongko@apapmedika.com")
                        .gender(false)
                        .yearsOfExperience(8)
                        .schedules(List.of(1, 3, 5)) // Monday, Wednesday, Friday
                        .fee(350000L)
                        .build(),
                Doctor.builder()
                        .id("UMM001")
                        .name("dr. Clara Wijaya")
                        .specialist(0)
                        .email("clara.wijaya@apapmedika.com")
                        .gender(true)
                        .yearsOfExperience(4)
                        .schedules(List.of(1, 2, 3, 4, 5, 6)) // Monday - Saturday
                        .fee(150000L)
                        .build(),
                Doctor.builder()
                        .id("GGI001")
                        .name("drg. Dimas Pratama")
                        .specialist(1)
                        .email("dimas.pratama@apapmedika.com")
                        .gender(false)
                        .yearsOfExperience(6)
                        .schedules(List.of(2, 4, 6)) // Tuesday, Thursday, Saturday
                        .fee(225000L)
                        .build(),
                Doctor.builder()
                        .id("JPD001")
                        .name("dr. Ratna Kusuma, Sp.JP")
                        .specialist(5)
                        .email("ratna.kusuma@apapmedika.com")
                        .gender(true)
                        .yearsOfExperience(12)
                        .schedules(List.of(1, 4)) // Monday, Thursday
                        .fee(400000L)
                        .build());

        doctorRepository.saveAll(doctors);
    }

    private void seedPatients() {
        if (patientRepository.count() > 0)
            return;

        Faker faker = new Faker(Locale.forLanguageTag("id-ID"));
        List<Patient> patients = new ArrayList<>();

        // Sample pasien tetap untuk kemudahan testing NIK
        patients.add(Patient.builder()
                .nik("3274010909600001")
                .name("Nurhasanah")
                .gender(true)
                .email("nurhasanah@example.com")
                .birthDate(LocalDate.of(1960, 9, 9))
                .birthPlace("Jakarta")
                .build());

        patients.add(Patient.builder()
                .nik("3171021505950002")
                .name("Ahmad Setiawan")
                .gender(false)
                .email("ahmad.setiawan@example.com")
                .birthDate(LocalDate.of(1995, 5, 15))
                .birthPlace("Bandung")
                .build());

        for (int i = 3; i <= 10; i++) {
            String nik = String.format("31740%011d", 10000000000L + i);
            LocalDate birthDate = faker.date().birthday(5, 65)
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            patients.add(Patient.builder()
                    .nik(nik)
                    .name(faker.name().fullName())
                    .gender(i % 2 == 0)
                    .email(faker.internet().emailAddress())
                    .birthDate(birthDate)
                    .birthPlace(faker.address().cityName())
                    .build());
        }

        patientRepository.saveAll(patients);
    }
}