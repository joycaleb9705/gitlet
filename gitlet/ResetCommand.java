package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;

/** Command for resetting.
 *  @author Sun Hyuk Ahn
 */
public class ResetCommand {

    /** Current directory.*/
    private static String userDir = System.getProperty("user.dir");

    /** Executes the reset command.
     * @param commitId id of the commit.*/
    public static void execute(String commitId) {
        BranchControl bCon = Gitlet.getBCon();
        CommitControl cCon = Gitlet.getCCon();
        Branch currBranch = bCon.getCurrBranch();
        HashMap<String, Commit> commits = cCon.getCommits();
        Staging stage = bCon.getCurrStage();
        if (!commits.containsKey(commitId)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit oldCom = currBranch.getHead();
        HashMap<String, File> oldBlob = oldCom.getBlob();
        HashSet<String> untracked = new HashSet<>();
        File userDire = new File(userDir);
        HashSet<String> ignore = bCon.getIgnore();
        Commit currCom = commits.get(commitId);
        HashMap<String, File> blob = currCom.getBlob();
        for (File curr : userDire.listFiles()) {
            String name = curr.getName();
            if (!ignore.contains(name)) {
                if (!oldBlob.containsKey(name)
                        && !stage.getAdd().containsKey(name)
                        && !stage.getRemove().contains(name)) {
                    untracked.add(name);
                }
            }
        }
        if (!untracked.isEmpty()) {
            System.out.println("There is an untracked "
                    + "file in the way; delete it or add it first.");
            return;
        }
        for (File curr : userDire.listFiles()) {
            String name = curr.getName();
            if (!ignore.contains(name)) {
                if (!blob.containsKey(name)) {
                    curr.delete();
                }
            }
        }
        for (String key : blob.keySet()) {
            File curr = blob.get(key);
            Path currPath = curr.toPath();
            Path copiedPath = Paths.get(key);
            try {
                Files.copy(currPath, copiedPath,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("Could not copy the file.");
                return;
            }
        }
        stage = bCon.getCurrStage();
        stage.getAdd().clear();
        stage.getRemove().clear();
        currBranch.setHead(currCom);
        bCon.serialize();
    }
}
