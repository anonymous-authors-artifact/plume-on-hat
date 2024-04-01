import alg.AlgType;
import alg.Plume;
import alg.PlumeList;
import alg.IsolationLevel;
import loader.ElleHistoryLoader;
import loader.TextHistoryLoader;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "Plume", mixinStandardHelpOptions = true, version = "Plume 1.0", description = "Check if the history satisfies transactional causal consistency.\n")
public class Main implements Callable<Integer> {

    @Parameters(index = "0", description = "Input file")
    private File file;

    @Option(names = "-t", description = "Candidates: ${COMPLETION-CANDIDATES}")
    private AlgType algType;

    @Option(names = "-i", description = "Candidates: ${COMPLETION-CANDIDATES}")
    private IsolationLevel isolationLevel;

    @Option(names = "--enable-graphviz", description = "Use graphviz to visualize violation")
    private boolean enableGraphviz;

    @Override
    public Integer call()  {
        if (algType.equals(AlgType.PLUME_LIST)) {
            var historyLoader = new ElleHistoryLoader(file);
            var history = historyLoader.loadHistory();
            var plume = new PlumeList<>(algType, history, isolationLevel, enableGraphviz);
            plume.validate();
            System.out.println(plume.getBadPatterns());
        } else {
            var historyLoader = new TextHistoryLoader(file);
            var history = historyLoader.loadHistory();
            var plume = new Plume<>(algType, history, isolationLevel, enableGraphviz);
            plume.validate();
            System.out.println(plume.getBadPatterns());
        }
        return 0;
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

}