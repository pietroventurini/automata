import files.FileUtils;
import graph.BFAnetwork.BFANetwork;
import graph.BFAnetwork.BFANetworkSupervisor;
import graph.BFAnetwork.BSState;
import graph.BFAnetwork.BSTransition;
import graph.BFAnetwork.DBSState;
import graph.BFAnetwork.DSCTransition;
import graph.BFAnetwork.Diagnostician;
import graph.BFAnetwork.LOBSState;
import graph.fa.AcceptedLanguages;
import graph.fa.FA;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;

@Disabled
public class SpatialComplexityTest {
    private static final int ITERATIONS = 100;
    private Map<BFANetwork, String> bfaNetworks;

    private List<String> generateLinearObservations(String project) {
        if (project.equals("test"))
            return List.of("o3", "o2");
        else if (project.equals("Network2"))
            return List.of("act", "sby", "nop");
        return null;
    }

    @BeforeEach
    public void setUp() {
        bfaNetworks = new HashMap<>();
        for (String project : FileUtils.getProjectsList()) {
            FileUtils fileUtils = new FileUtils(project);
            try {
                bfaNetworks.put(fileUtils.loadBFANetwork(), project);
            } catch (IOException e) {
            }
        }
    }

    @Test
    public void computeBehavioralSpace() {
        System.out.println("Computation of behvioral spaces... ");
        for (BFANetwork bfaNetwork : bfaNetworks.keySet()) {
            double avg = 0;
            FA<BSState, BSTransition> bs;
            for (int i = 1; i <= ITERATIONS; i++) {
                System.gc();
                double memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                bs = BFANetworkSupervisor.getBehavioralSpace(bfaNetwork);
                BFANetworkSupervisor.pruneFA(bs);
                System.gc();
                double memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                avg = avg + ((memoryAfter - memoryBefore) - avg) / i;
            }
            System.out
                    .println("Average memory for " + bfaNetworks.get(bfaNetwork) + ": " + avg / (1024 * 1024) + " MB");
        }
        System.out.println("\n");
        assertTrue(true);
    }

    @Test
    public void computeDecoratedSpace() {
        System.out.println("Computation of decorated spaces... ");
        for (BFANetwork bfaNetwork : bfaNetworks.keySet()) {
            double avg = 0;
            FA<BSState, BSTransition> bs = BFANetworkSupervisor.getBehavioralSpace(bfaNetwork);
            BFANetworkSupervisor.pruneFA(bs);
            FA<FA<DBSState, BSTransition>, DSCTransition> ds;
            for (int i = 1; i <= ITERATIONS; i++) {
                System.gc();
                double memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                ds = BFANetworkSupervisor.decoratedSpaceOfClosures(bs);
                System.gc();
                double memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                avg = avg + ((memoryAfter - memoryBefore) - avg) / i;
            }
            System.out
                    .println("Average memory for " + bfaNetworks.get(bfaNetwork) + ": " + avg / (1024 * 1024) + " MB");
        }
        System.out.println("\n");
        assertTrue(true);
    }

    @Test
    public void computeDiagnostician() {
        System.out.println("Computation of diagnosticians... ");
        for (BFANetwork bfaNetwork : bfaNetworks.keySet()) {
            double avg = 0;
            FA<BSState, BSTransition> bs = BFANetworkSupervisor.getBehavioralSpace(bfaNetwork);
            BFANetworkSupervisor.pruneFA(bs);
            FA<FA<DBSState, BSTransition>, DSCTransition> ds = BFANetworkSupervisor.decoratedSpaceOfClosures(bs);
            Diagnostician d;
            for (int i = 1; i <= ITERATIONS; i++) {
                System.gc();
                double memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                d = BFANetworkSupervisor.diagnostician(ds);
                System.gc();
                double memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                avg = avg + ((memoryAfter - memoryBefore) - avg) / i;
            }
            System.out
                    .println("Average memory for " + bfaNetworks.get(bfaNetwork) + ": " + avg / (1024 * 1024) + " MB");
        }
        System.out.println("\n");
        assertTrue(true);
    }

    @Test
    public void computeDiagnosisiWithLOBS() {
        System.out.println("Computation of diagnosis with LOBS... ");
        for (BFANetwork bfaNetwork : bfaNetworks.keySet()) {
            double avg = 0;
            List<String> linObs = generateLinearObservations(bfaNetworks.get(bfaNetwork));
            FA<LOBSState, BSTransition> lobs;
            for (int i = 1; i <= ITERATIONS; i++) {
                try {
                    System.gc();
                    double memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    lobs = BFANetworkSupervisor.getBehavioralSpaceForLinearObservation(bfaNetwork, linObs);
                    BFANetworkSupervisor.pruneFA(lobs);
                    System.gc();
                    double memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    avg = avg + ((memoryAfter - memoryBefore) - avg) / i;
                } catch (InvalidAlgorithmParameterException e) {
                }
            }
            System.out
                    .println("Average memory for " + bfaNetworks.get(bfaNetwork) + ": " + avg / (1024 * 1024) + " MB");
        }
        System.out.println("\n");
        assertTrue(true);
    }

    private void writeCSV(List<Double> values, String filename) {
        try (FileWriter writer = new FileWriter("src/test/java/csv/" + filename)) {
            String output = "";
            for (double value : values) {
                output += value + ",";
                // writer.append(System.lineSeparator());
            }
            output = output.substring(0, output.length() - 1);
            writer.append(output);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
