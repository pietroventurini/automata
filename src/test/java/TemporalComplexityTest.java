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

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;

@Disabled
public class TemporalComplexityTest {
    private static final int ITERATIONS = 10000;
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
            for (int i = 1; i <= ITERATIONS; i++) {
                long start = System.nanoTime();
                BFANetworkSupervisor.pruneFA(BFANetworkSupervisor.getBehavioralSpace(bfaNetwork));
                long end = System.nanoTime();
                avg = avg + ((end - start) - avg) / i;
            }
            System.out.println("Average time for " + bfaNetworks.get(bfaNetwork) + ": " + avg / 1000000 + " ms");
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
            for (int i = 1; i <= ITERATIONS; i++) {
                long start = System.nanoTime();
                BFANetworkSupervisor.decoratedSpaceOfClosures(bs);
                long end = System.nanoTime();
                avg = avg + ((end - start) - avg) / i;
            }
            System.out.println("Average time for " + bfaNetworks.get(bfaNetwork) + ": " + avg / 1000000 + " ms");
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
            for (int i = 1; i <= ITERATIONS; i++) {
                long start = System.nanoTime();
                BFANetworkSupervisor.diagnostician(ds);
                long end = System.nanoTime();
                avg = avg + ((end - start) - avg) / i;
            }
            System.out.println("Average time for " + bfaNetworks.get(bfaNetwork) + ": " + avg / 1000000 + " ms");
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
            for (int i = 1; i <= ITERATIONS; i++) {
                try {
                    long start = System.nanoTime();
                    FA<LOBSState, BSTransition> lobs = BFANetworkSupervisor
                            .getBehavioralSpaceForLinearObservation(bfaNetwork, linObs);
                    BFANetworkSupervisor.pruneFA(lobs);
                    AcceptedLanguages.reduceFAtoMultipleRegex(lobs);
                    long end = System.nanoTime();
                    avg = avg + ((end - start) - avg) / i;
                } catch (InvalidAlgorithmParameterException e) {
                }
            }
            System.out.println("Average time for " + bfaNetworks.get(bfaNetwork) + ": " + avg / 1000000 + " ms");
        }
        System.out.println("\n");
        assertTrue(true);
    }

    @Test
    public void computeDiagnosisiWithDiagnostician() {
        System.out.println("Computation of diagnosis with diagnostician... ");
        for (BFANetwork bfaNetwork : bfaNetworks.keySet()) {
            double avg = 0;
            FA<BSState, BSTransition> bs = BFANetworkSupervisor.getBehavioralSpace(bfaNetwork);
            BFANetworkSupervisor.pruneFA(bs);
            FA<FA<DBSState, BSTransition>, DSCTransition> ds = BFANetworkSupervisor.decoratedSpaceOfClosures(bs);
            Diagnostician d = BFANetworkSupervisor.diagnostician(ds);
            List<String> linObs = generateLinearObservations(bfaNetworks.get(bfaNetwork));
            for (int i = 1; i <= ITERATIONS; i++) {
                try {
                    long start = System.nanoTime();
                    BFANetworkSupervisor.linearDiagnosis(d, linObs);
                    long end = System.nanoTime();
                    avg = avg + ((end - start) - avg) / i;
                } catch (InvalidAlgorithmParameterException e) {
                }
            }
            System.out.println("Average time for " + bfaNetworks.get(bfaNetwork) + ": " + avg / 1000000 + " ms");
        }
        System.out.println("\n");
        assertTrue(true);
    }
}
