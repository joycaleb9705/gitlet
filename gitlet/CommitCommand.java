package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;

/** Command for committing.
 * @author Sun Hyuk Ahn*/
public class CommitCommand {

    /** Branch Control.*/
    private static BranchControl braCon = Gitlet.getBCon();
    /** Commit Control.*/
    private static CommitControl comCon = Gitlet.getCCon();
    /** Current stage.*/
    private static Staging currStage = braCon.getCurrStage();
    /** Current directory.*/
    private static String userDir = System.getProperty("user.dir");

    /** Executes the commit command.
     * @param message msg
     * @param merged merge
     */
    public static void execute(String message, boolean merged) {
        HashMap<String, File> add = currStage.getAdd();
        HashSet<String> remove = currStage.getRemove();
        if (!initExecute(message, add, remove)) {
            return;
        }
        Commit currCom = braCon.getCurrBranch().getHead();
        HashMap<String, File> newBlob = new HashMap<String, File>();
        Commit nextCom = comCon.newCommit(message, currCom, newBlob);
        if (merged) {
            nextCom.setMerge(true);
        }
        int nextId = comCon.getCommitCount() - 1;
        String newDir = userDir + "/.gitlet/commit" + Integer.toString(nextId);
        try {
            Files.createDirectories(Paths.get(newDir));
        } catch (IOException e) {
            System.out.println("Could not create the directory.");
            return;
        }
        if (!add.isEmpty()) {
            for (String key : add.keySet()) {
                File curr = add.get(key);
                String copiedDir = newDir + "/" + curr.getName();
                File copiedFile = new File(copiedDir);
                newBlob.put(key, copiedFile);
                Path currPath = Paths.get(curr.toString());
                Path copiedPath = Paths.get(copiedDir);
                try {
                    Files.copy(currPath, copiedPath,
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.out.println("Could not copy the file.");
                    return;
                }
            }
        }
        HashMap<String, File> blob = currCom.getBlob();
        newBlob = updateBlob(blob, newBlob, remove);
        HashSet<String> removed = currStage.getRemoved();
        if (!removed.isEmpty()) {
            for (String key : removed) {
                if (newBlob.containsKey(key)) {
                    newBlob.remove(key);
                }
            }
        }
        nextCom.setBlob(newBlob);
        braCon.getCurrBranch().setHead(nextCom);
        add.clear();
        remove.clear();
        removed.clear();
        comCon.serialize();
        braCon.serialize();
    }

    /** Updates the new blob.
     * @param blob current blob
     * @param newB new blob
     * @param remove files to remove
     * @return updated new blob*/
    public static HashMap<String, File> updateBlob(HashMap<String, File> blob,
                                                   HashMap<String, File> newB,
                                                   HashSet<String> remove) {
        if (!blob.isEmpty()) {
            for (String key : blob.keySet()) {
                if (!newB.containsKey(key)) {
                    File currFile = blob.get(key);
                    newB.put(key, currFile);
                }
            }
        }
        if (!remove.isEmpty()) {
            for (String key : remove) {
                if (newB.containsKey(key)) {
                    newB.remove(key);
                }
            }
        }
        return newB;
    }

    /** Takes care of the initial cases.
     * @param msg message
     * @param add staged files
     * @param remove removing files
     * @return continue or no*/
    public static boolean initExecute(String msg, HashMap<String, File> add,
                                      HashSet<String> remove) {
        if (msg.equals("")) {
            System.out.println("Please enter a commit message.");
            return false;
        }
        if (add.isEmpty() && remove.isEmpty()) {
            System.out.println("No changes added to the commit");
            return false;
        }
        return true;
    }

}
