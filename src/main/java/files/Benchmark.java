package files;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * It represents a single operation done on the network, for instance, the
 * computation of the behavioral space. Each Benchmark consists of: - the date
 * when the task has been performed - the description of the task - the duration
 * of the task expressed in nanoseconds (use System.nanoTime() to record the
 * current time in ns).
 */
public class Benchmark {

    // constants to build the description
    public static final String BS = "Computation of the behavioral space ";
    public static final String OF_LINOBS = "relating to the linear observation ";
    public static final String DIAGNOSIS_CALC = "Computation of the diagnosis ";
    public static final String DIAGNOSIS = "The diagnosis is ";
    public static final String SILENT_CLOSURE = "Computation of the silent closure ";
    public static final String DSC = "Computation of the decorated space of closures";
    public static final String OF_STATE = "of state ";
    public static final String DIAGNOSTICIAN = "Computation of the diagnostician";
    public static final String USING_DIAGNOSTICIAN = "using the diagnostician";

    LocalDateTime date;
    String description;
    long duration;

    public Benchmark(LocalDateTime date, String description, long duration) {
        this.date = date;
        this.description = description;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Date: " + date.format(DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss")) + "\n" +
                "Operation: " + description + "\n" +
                "Duration: " + duration / 1000000 + " ms\n";
    }
}
