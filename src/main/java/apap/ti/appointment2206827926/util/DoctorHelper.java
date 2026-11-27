package apap.ti.appointment2206827926.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DoctorHelper {

    private static final Map<Integer, String> SPECIALIZATION_NAMES = new LinkedHashMap<>();
    private static final Map<Integer, String> SPECIALIZATION_CODES = new LinkedHashMap<>();
    private static final Map<Integer, String> DAY_NAMES = new LinkedHashMap<>();

    static {
        SPECIALIZATION_NAMES.put(0, "Dokter Umum (dr.)");
        SPECIALIZATION_NAMES.put(1, "Dokter Gigi (drg.)");
        SPECIALIZATION_NAMES.put(2, "Spesialis Anak (Sp.A)");
        SPECIALIZATION_NAMES.put(3, "Bedah (Sp.B)");
        SPECIALIZATION_NAMES.put(4, "Bedah Plastik, Rekonstruksi, dan Estetik (Sp.BP-RE)");
        SPECIALIZATION_NAMES.put(5, "Jantung dan Pembuluh Darah (Sp.JP)");
        SPECIALIZATION_NAMES.put(6, "Kulit dan Kelamin (Sp.KK)");
        SPECIALIZATION_NAMES.put(7, "Mata (Sp.M)");
        SPECIALIZATION_NAMES.put(8, "Obstetri dan Ginekologi (Sp.OG)");
        SPECIALIZATION_NAMES.put(9, "Penyakit Dalam (Sp.PD)");
        SPECIALIZATION_NAMES.put(10, "Paru (Sp.P)");
        SPECIALIZATION_NAMES.put(11, "Telinga, Hidung, Tenggorokan, Bedah Kepala Leher (Sp.THT-KL)");
        SPECIALIZATION_NAMES.put(12, "Radiologi (Sp.Rad)");
        SPECIALIZATION_NAMES.put(13, "Kesehatan Jiwa (Sp.KJ)");
        SPECIALIZATION_NAMES.put(14, "Anestesi (Sp.An)");
        SPECIALIZATION_NAMES.put(15, "Neurologi (Sp.N)");
        SPECIALIZATION_NAMES.put(16, "Urologi (Sp.U)");

        SPECIALIZATION_CODES.put(0, "UMM");
        SPECIALIZATION_CODES.put(1, "GGI");
        SPECIALIZATION_CODES.put(2, "ANK");
        SPECIALIZATION_CODES.put(3, "BDH");
        SPECIALIZATION_CODES.put(4, "PRE");
        SPECIALIZATION_CODES.put(5, "JPD");
        SPECIALIZATION_CODES.put(6, "KKL");
        SPECIALIZATION_CODES.put(7, "MTA");
        SPECIALIZATION_CODES.put(8, "OBG");
        SPECIALIZATION_CODES.put(9, "PDL");
        SPECIALIZATION_CODES.put(10, "PRU");
        SPECIALIZATION_CODES.put(11, "THT");
        SPECIALIZATION_CODES.put(12, "RAD");
        SPECIALIZATION_CODES.put(13, "KSJ");
        SPECIALIZATION_CODES.put(14, "ANS");
        SPECIALIZATION_CODES.put(15, "NRO");
        SPECIALIZATION_CODES.put(16, "URO");

        DAY_NAMES.put(1, "Monday");
        DAY_NAMES.put(2, "Tuesday");
        DAY_NAMES.put(3, "Wednesday");
        DAY_NAMES.put(4, "Thursday");
        DAY_NAMES.put(5, "Friday");
        DAY_NAMES.put(6, "Saturday");
        DAY_NAMES.put(7, "Sunday");
    }

    public static Map<Integer, String> getAllSpecializations() {
        return SPECIALIZATION_NAMES;
    }

    public static Map<Integer, String> getAllDays() {
        return DAY_NAMES;
    }

    public static String getSpecializationName(int specialist) {
        return SPECIALIZATION_NAMES.getOrDefault(specialist, "Unknown");
    }

    public static String getSpecializationCode(int specialist) {
        return SPECIALIZATION_CODES.getOrDefault(specialist, "UMM");
    }

    public static String getDayName(int day) {
        return DAY_NAMES.getOrDefault(day, "Unknown");
    }

    public static String formatSchedules(List<Integer> schedules) {
        if (schedules == null || schedules.isEmpty()) {
            return "-";
        }
        return schedules.stream()
                .distinct()
                .sorted()
                .map(DoctorHelper::getDayName)
                .collect(Collectors.joining(", "));
    }

    public static String formatDoctorName(String rawName, int specialist) {
        if (rawName == null || rawName.isBlank()) {
            return "";
        }

        // Bersihkan gelar lama jika user sudah terlanjur mengetik dr./drg. atau ,
        // Sp.XXX
        String baseName = rawName.trim()
                .replaceFirst("(?i)^(drg?\\.\\s*)+", "")
                .replaceFirst("(?i),\\s*Sp\\.[A-Za-z-]+$", "")
                .trim();

        String prefix = (specialist == 1) ? "drg. " : "dr. ";
        String specFull = getSpecializationName(specialist);
        String suffix = "";

        int openIdx = specFull.indexOf("(Sp.");
        int closeIdx = specFull.indexOf(')', openIdx);
        if (openIdx != -1 && closeIdx != -1) {
            suffix = ", " + specFull.substring(openIdx + 1, closeIdx);
        }

        return prefix + baseName + suffix;
    }
}