package files;

import com.google.gson.Gson;
import graph.BFAnetwork.BFANetwork;
import graph.bfa.BFA;
import graph.fa.FA;
import graph.fa.FAState;
import graph.fa.Transition;
import graph.nodes.State;

import java.io.*;

/**
 * This is a support class needed for storing different types of objects to files and for loading them back.
 * When you initialize a FileUtils object, you need to specify the name of the project which will be used
 * as the directory's name that will contain the files (fa.json, bfa.json, bfa_network.json, ...)
 *

 */
public class FileUtils {

    private static final String FA_DIR = "FAs/";
    private static final String BFA_DIR = "BFAs/";
    private static final String BFANETWORK_JSON = "bfa_network.json";
    private static final String CURRENT_DIR = "";

    private static final String FILES_ROOT = "files/";
    private String path;
    private Gson gson = new Gson();

    /**
     * Instantiate a FileUtils and set the working directory as "files/{projectName}/"
     * @param projectName the name of the folder containing the files relating to a project
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
     * Convert a FA into json format
     */
    private <S extends State, T extends Transition> String faToJson(FA<S,T> fa) {
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
     * Store {@code fa} in a file in json format. The file will be called "{FA_JSON}"
     * where {FA_JSON} is a predefined constant (e.g. "fa.json"), and placed in the projectFolder, which has been specified
     * when instantiating the FileUtils object;
     */
    public void storeFA(FA<FAState, Transition> fa) {
        String json = faToJson(fa);
        jsonToFile(json, FA_DIR, fa.getName().concat(".json"));
    }

    /**
     * Store {@code bfa} in a file in json format. The file will be called "{BFA_JSON}"
     * where {BFA_JSON} is a predefined constant (e.g. "bfa.json"), and placed in the projectFolder,
     * which has been specified when instantiating the FileUtils object;
     */
    public void storeBFA(BFA bfa) {
        String json = bfaToJson(bfa);
        jsonToFile(json, BFA_DIR, bfa.getName().concat(".json"));
    }

    /**
     * Store {@code bfaNetwork} in a file in json format. The file will be called "{BFANETWORK_JSON}"
     * where {BFANETWORK_JSON} is a predefined constant (e.g. "bfa_network.json"), and placed in the projectFolder,
     * which has been specified when instantiating the FileUtils object;
     */
    public void storeBFANetwork(BFANetwork bfaNetwork) {
        String json = bfaNetworkToJson(bfaNetwork);
        jsonToFile(json, CURRENT_DIR, BFANETWORK_JSON);
    }


    /**
     * load the FA which is encoded as a json file in the projectFolder
     * @param name The name of the FA to be loaded (e.g. "x0").
     *             Note: the extension ".json" must NOT be included in the name
     */
    public FA<FAState, Transition> loadFA(String name) {
        File file = getFile(FA_DIR, name.concat(".json"));
        try (Reader reader = new FileReader(file)) {
            FAJson faJson = gson.fromJson(reader, FAJson.class);
            return faJson.toFA();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * load the BFA which is encoded as a json file in the projectFolder
     */
    public BFA loadBFA(String name) {
        File file = getFile(BFA_DIR, name.concat(".json"));
        try (Reader reader = new FileReader(file)) {
            BFAJson faJson = gson.fromJson(reader, BFAJson.class);
            return faJson.toBFA();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * load the BFANetwork which is encoded as a json file in the projectFolder
     */
    public BFANetwork loadBFANetwork() {
        File file = getFile(CURRENT_DIR, BFANETWORK_JSON);
        try (Reader reader = new FileReader(file)) {
            BFANetworkJson faJson = gson.fromJson(reader, BFANetworkJson.class);
            return faJson.toBFANetwork();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * save the {@code json} object into file {directory/fileName}.
     * fileName must include the extension (e.g. myFile.json)
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
     * Returns a File given {@code directory } (which is its position) and its {@code fileName}.
     * If any of the folder in the path does not exists, then it creates them.
     */
    private File getFile(String directory, String fileName) {
        File dir = new File(path + directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir.getPath() + '/' + fileName);
    }
}
