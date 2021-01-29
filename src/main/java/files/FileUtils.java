package files;

import com.google.common.io.Files;
import com.google.gson.Gson;
import graph.BFAnetwork.BFANetwork;
import graph.bfa.BFA;
import graph.fa.FA;
import graph.fa.FAState;
import graph.fa.Transition;
import graph.nodes.State;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static files.Benchmark.*;

/**
 * This is a support class needed for storing different types of objects to
 * files and for loading them back. When you initialize a FileUtils object, you
 * need to specify the name of the project which will be used as the directory's
 * name that will contain the sub-folders and the files. All projects are stored
 * inside "files/".
 *
 * For example, the structure of a project could be:
 *
 * project_name/ FAs/ fa1.json fa2.json BFAs/ bfa1.json bfa2.json
 * bfa_network.json
 */
public class FileUtils {

    private static final String FA_DIR = "FAs/";
    private static final String BFA_DIR = "BFAs/";
    private static final String BFANETWORK_JSON = "bfa_network.json";
    private static final String BENCHMARKS_JSON = "benchmarks.json";
    private static final String CURRENT_DIR = "";
    private static final String FILES_ROOT = "files/";

    private String path;
    private Gson gson = new Gson();

    /**
     * Instantiate a FileUtils and set the working directory as
     * "files/{projectName}/"
     * 
     * @param projectName the name of the folder containing the files relating to a
     *                    project
     */
    public FileUtils(String projectName) {
        this.path = FILES_ROOT + projectName + "/";
    }

    public String path() {
        return path;
    }

    public void setProjectName(String projectName) {
        this.path = FILES_ROOT + projectName + '/';
    }

    /**
     * Returns a list containing the names of the existing projects
     */
    public static List<String> getProjectsList() {
        File dir = new File(FILES_ROOT);
        if (!dir.exists()) {
            dir.mkdir();
        }

        FilenameFilter dirFilter = (dir1, name) -> dir1.isDirectory() && !name.equals(".DS_Store");
        return Stream.of(dir.list(dirFilter)).collect(Collectors.toList());
    }

    /**
     * Returns a list containing the names of the BFAs that are currently stored in
     * a file for the current project.
     */
    public List<String> getBFAsList() {
        File dir = new File(path + BFA_DIR);
        if (!dir.exists())
            return Collections.EMPTY_LIST;
        FilenameFilter nameFilter = (dir1, name) -> name.toLowerCase().endsWith(".json");
        return Stream.of(dir.listFiles(nameFilter)).filter(file -> !file.isDirectory())
                .map(f -> Files.getNameWithoutExtension(f.getName())).collect(Collectors.toList());
    }

    /**
     * Convert a FA into json format
     */
    private <S extends State, T extends Transition> String faToJson(FA<S, T> fa) {
        FAJson faJson = new FAJson(fa);
        return gson.toJson(faJson);
    }

    /**
     * Convert a BFA into json format
     */
    private String bfaToJson(BFA bfa) {
        BFAJson bfaJson = new BFAJson(bfa);
        return gson.toJson(bfaJson);
    }

    /**
     * Convert a BFANetwork into json format
     */
    private String bfaNetworkToJson(BFANetwork net) {
        BFANetworkJson netJson = new BFANetworkJson(net);
        return gson.toJson(netJson);
    }

    /**
     * Store {@code fa} in a file in json format. The file will be put in the
     * project directory inside {@code FA_JSON} where {@code FA_JSON} is a
     * predefined constant (e.g. "FAs/").
     */
    public void storeFA(FA<FAState, Transition> fa) {
        String json = faToJson(fa);
        jsonToFile(json, FA_DIR, fa.getName().concat(".json"));
    }

    /**
     * Store {@code bfa} in a file in json format. The file will be put in the
     * project directory inside {@code BFA_JSON} where {@code BFA_JSON} is a
     * predefined constant (e.g. "BFAs/").
     */
    public void storeBFA(BFA bfa) {
        String json = bfaToJson(bfa);
        jsonToFile(json, BFA_DIR, bfa.getName().concat(".json"));
    }

    /**
     * Store {@code bfaNetwork} in a file in json format. The file will be called
     * "{@code BFANETWORK_JSON}" where {@code BFANETWORK_JSON} is a predefined
     * constant (e.g. "bfa_network.json"), and placed in the projectFolder, which
     * has been specified when instantiating the FileUtils object;
     */
    public void storeBFANetwork(BFANetwork bfaNetwork) {
        String json = bfaNetworkToJson(bfaNetwork);
        jsonToFile(json, CURRENT_DIR, BFANETWORK_JSON);
    }

    /**
     * Store {@code benchmarks} in a file in json format. The file will be called
     * "{@code BENCHMARKS_JSON}" where {@code BENCHMARKS_JSON} is a predefined
     * constant (e.g. "benchmarks.json"), and placed in the projectFolder, which has
     * been specified when instantiating the FileUtils object;
     */
    private void storeBenchmarks(List<Benchmark> benchmarks) {
        String json = gson.toJson(benchmarks.toArray(Benchmark[]::new));
        jsonToFile(json, CURRENT_DIR, BENCHMARKS_JSON);
    }

    /**
     * store the benchmark of the computation of the behavioral space. Duration has
     * to be in ns.
     */
    public void storeBSBenchmark(long duration) {
        List<Benchmark> bms = loadBenchmarks();
        Benchmark b = new Benchmark(LocalDateTime.now(), BS, duration);
        bms.add(b);
        storeBenchmarks(bms);
    }

    /**
     * Store the benchmark of the computation of the behavioral space relating to a
     * linear observation. Duration has to be in ns.
     */
    public void storeBSOfLinObsBenchmark(long duration, List<String> linObs) {
        List<Benchmark> bms = loadBenchmarks();
        String desc = BS + OF_LINOBS + linObs.toString();
        Benchmark b = new Benchmark(LocalDateTime.now(), desc, duration);
        bms.add(b);
        storeBenchmarks(bms);
    }

    /**
     * Store the benchmark of the computation of the diagnosis relating to a linear
     * observation. Duration has to be in ns.
     */
    public void storeDiagnosisOfLinObsBenchmark(long duration, List<String> linObs, String diagnosis) {
        List<Benchmark> bms = loadBenchmarks();
        String desc = DIAGNOSIS_CALC + OF_LINOBS + linObs.toString() + ".\n" + DIAGNOSIS + diagnosis;
        Benchmark b = new Benchmark(LocalDateTime.now(), desc, duration);
        bms.add(b);
        storeBenchmarks(bms);
    }

    /**
     * Store the benchmark of the computation of the silent closure relating to a
     * certain state. Duration has to be in ns.
     */
    public void storeSilentClosureBenchmark(long duration, String stateName) {
        List<Benchmark> bms = loadBenchmarks();
        String desc = SILENT_CLOSURE + OF_STATE + stateName;
        Benchmark b = new Benchmark(LocalDateTime.now(), desc, duration);
        bms.add(b);
        storeBenchmarks(bms);
    }

    /**
     * Store the benchmark of the computation of the decorated space of closures.
     * Duration has to be in ns.
     */
    public void storeDSCBenchmark(long duration) {
        List<Benchmark> bms = loadBenchmarks();
        String desc = DSC;
        Benchmark b = new Benchmark(LocalDateTime.now(), desc, duration);
        bms.add(b);
        storeBenchmarks(bms);
    }

    /**
     * Store the benchmark of the computation of the diagnostician. Duration has to
     * be in ns.
     */
    public void storeDiagnosticianBenchmark(long duration) {
        List<Benchmark> bms = loadBenchmarks();
        String desc = DIAGNOSTICIAN;
        Benchmark b = new Benchmark(LocalDateTime.now(), desc, duration);
        bms.add(b);
        storeBenchmarks(bms);
    }

    /**
     * Store the benchmark of the computation of the diagnosis relating to a linear
     * observation using the diagnostician. Duration has to be in ns.
     */
    public void storeDiagnosisOfLinObsWithDiagnosticianBenchmark(long duration, List<String> linObs, String diagnosis) {
        List<Benchmark> bms = loadBenchmarks();
        String desc = DIAGNOSIS_CALC + OF_LINOBS + linObs.toString() + USING_DIAGNOSTICIAN + ".\n" + DIAGNOSIS
                + diagnosis;
        Benchmark b = new Benchmark(LocalDateTime.now(), desc, duration);
        bms.add(b);
        storeBenchmarks(bms);
    }

    /**
     * load the FA which is encoded as a json file in the project directory under
     * the {FA_DIR} directory.
     * 
     * @param name The name of the FA to be loaded (e.g. "x0"). Note: the extension
     *             ".json" must NOT be included in the name
     */
    public FA<FAState, Transition> loadFA(String name) throws IOException {
        File file = getFile(FA_DIR, name.concat(".json"));
        try (Reader reader = new FileReader(file)) {
            FAJson faJson = gson.fromJson(reader, FAJson.class);
            if (faJson != null) {
                return faJson.toFA();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IOException();
    }

    /**
     * load the BFA which is encoded as a json file in the project directory under
     * the {BFA_DIR} directory.
     */
    public BFA loadBFA(String name) throws IOException {
        File file = getFile(BFA_DIR, name.concat(".json"));
        try (Reader reader = new FileReader(file)) {
            BFAJson bfaJson = gson.fromJson(reader, BFAJson.class);
            if (bfaJson != null) {
                return bfaJson.toBFA();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IOException();
    }

    /**
     * load the BFANetwork which is encoded as a json file in the project directory
     */
    public BFANetwork loadBFANetwork() throws IOException {
        File file = getFile(CURRENT_DIR, BFANETWORK_JSON);
        try (Reader reader = new FileReader(file)) {
            BFANetworkJson netJson = gson.fromJson(reader, BFANetworkJson.class);
            if (netJson != null) {
                return netJson.toBFANetwork();
            }        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IOException();
    }

    /**
     * load the Benchmarks which are encoded in a json file called
     * {@code BENCHMARKS_JSON} in the project directory
     */
    public List<Benchmark> loadBenchmarks() {
        File file = getFile(CURRENT_DIR, BENCHMARKS_JSON);
        List<Benchmark> benchmarks = new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Benchmark[] deserialized = gson.fromJson(reader, Benchmark[].class);
            if (deserialized != null) {
                benchmarks = new ArrayList<Benchmark>(Arrays.asList(deserialized));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return benchmarks;
    }

    /**
     * Save the {@code json} object into file "{directory}/{fileName}" Note:
     * fileName must include the extension (e.g. "myFile.json")
     */
    private void jsonToFile(String json, String directory, String fileName) {
        File file = getFile(directory, fileName);
        try (Writer fw = new FileWriter(file)) {
            fw.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a File given {@code directory} (which is its position) and its
     * {@code fileName}. If any of the folder in the path does not exists, then it
     * creates them.
     */
    private File getFile(String directory, String fileName) {
        File dir = new File(path + directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir.getPath() + '/' + fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}