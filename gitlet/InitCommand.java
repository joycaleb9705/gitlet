package gitlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/** Command for initializing the git repo.
 * @author Sun Hyuk Ahn*/
public class InitCommand {

    /** Current directory.*/
    private static String userDir = System.getProperty("user.dir");

    /** If initialized or not.*/
    private static boolean initialized;

    /** Executes the command.
     * @param directory the directory of the repo.*/
    public static void execute(String directory) throws IOException {
        Path newPath = Paths.get(directory, ".gitlet");
        if (newPath.toFile().exists()) {
            System.out.println("A Gitlet version-control "
                    + "system already exists in the current directory.");
            return;
        }
        Files.createDirectories(newPath);
        String fullPath = newPath.toString();
        Path newCommitPath = Paths.get(fullPath, "commit0");
        Files.createDirectories(newCommitPath);
        CommitControl comCon = new CommitControl(fullPath);
        HashMap<String, Commit> commits = comCon.getCommits();
        Commit curr = null;
        for (String key : commits.keySet()) {
            curr = commits.get(key);
        }
        BranchControl branchCon = new BranchControl(fullPath, curr);
        initialized = true;
    }
}
