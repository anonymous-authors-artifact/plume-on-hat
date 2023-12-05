import alg.AlgType;
import alg.Plume;
import alg.PlumeList;
import alg.IsolationLevel;
import loader.ElleHistoryLoader;
import loader.TextHistoryLoader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

public class TestReproduce {
    private final String reproduceAllPath = "../History/reproduce";

    private final String newBugsPath = "History/bugs/";

    private final String ednTestcasePath = "History/testcase/elle/";

    private final String txtTestcasePath = "History/testcase/text/";


    private static int count = 0;

    private static final Map<String, Integer> sumMap = new HashMap<>();

    private void runFile(File file) {
        if (file.getName().endsWith(".txt")) {
            System.out.println(file.getAbsolutePath());
            var historyLoader = new TextHistoryLoader(file);
            var history = historyLoader.loadHistory();
            var plume = new Plume<>(AlgType.PLUME, history, IsolationLevel.TCC, false);
            plume.validate();
            if(!plume.getBadPatternCount().isEmpty()) {
                plume.getBadPatternCount().forEach((k, v) -> sumMap.merge(k, 1, Integer::sum));
                count += 1;
            }
        }
        if (file.getName().endsWith(".edn")) {
            System.out.println(file.getAbsolutePath());
            var historyLoader = new ElleHistoryLoader(file);
            var history = historyLoader.loadHistory();
            var plume = new PlumeList<>(AlgType.PLUME_LIST, history, IsolationLevel.TCC, false);
            plume.validate();
            if(!plume.getBadPatternCount().isEmpty()) {
                plume.getBadPatternCount().forEach((k, v) -> sumMap.merge(k, 1, Integer::sum));
                count += 1;
            }
        }
    }

    private void traverseFolder(String folderPath) {
        File folder = new File(folderPath);
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        traverseFolder(file.getAbsolutePath());
                    } else {
                        runFile(file);
                    }
                }
            }
        }
    }

    @Test
    void reproduceKnownBugs() {
        traverseFolder(reproduceAllPath);
        List<String> keys = new ArrayList<>(sumMap.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            int val = sumMap.get(key);
            System.out.print(key + ":" + val + ";");
        }
    }

    @Test
    void reproduceNewBugs() {
        traverseFolder(newBugsPath);
        List<String> keys = new ArrayList<>(sumMap.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            int val = sumMap.get(key);
            System.out.print(key + ":" + val + ";");
        }
    }

    @Test
    void ednTestcase() {
        traverseFolder(ednTestcasePath);
        System.out.println(count);
        System.out.println(sumMap);
    }

    @Test
    void txtTestcase() {
        traverseFolder(txtTestcasePath);
        System.out.println(count);
        System.out.println(sumMap);
    }
}
