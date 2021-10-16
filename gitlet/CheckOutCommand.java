package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;

/** ChecksOut according to the given parameters.
 * @author Sun Hyuk Ahn*/
public class CheckOutCommand {

    /** Current directory.*/
    private static String userDir = System.getProperty("user.dir");
    /** Current branch control.*/
    private static BranchControl bCon = Gitlet.getBCon();
    /** Files we ignore.*/
    private static HashSet<String> ignore = bCon.getIgnore();
    /** Current stage.*/
    private static Staging currStage = bCon.getCurrStage();

    /** Executes checkout.
     * @param arg1 arg[1]
     * @param arg2 arg[2]
     * @param arg3 arg[3]*/
    public static void execute(String arg1, String arg2, String arg3) {
        Branch currBranch = bCon.getCurrBranch();
        Commit currCommit = currBranch.getHead();
        if (arg1.equals("--")) {
            executeOne(currCommit, arg2);
        } else if (arg2.equals("--")) {
            executeTwo(arg1, arg3, currCommit);
        } else {
            executeThree(arg1, currBranch, currCommit);
        }
    }

    /** First case of check out.
     * @param currCommit current commit
     * @param arg2 argument 2*/
    public static void executeOne(Commit currCommit, String arg2) {
        String fileName = arg2;
        if (!currCommit.hasFile(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File currFile = currCommit.getFile(fileName);
        Path currPath = currFile.toPath();
        Path copiedPath = Paths.get(userDir, fileName);
        try {
            Files.copy(currPath, copiedPath,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Could not copy the file.");
            return;
        }
        bCon.serialize();
    }

    /** Second case of check out.
     * @param arg1 argument 1
     * @param arg3 argument 3
     * @param currCommit current commit*/
    public static void executeTwo(String arg1, String arg3, Commit currCommit) {
        CommitControl cCon = Gitlet.getCCon();
        String commitId = arg1;
        String fileName = arg3;
        currCommit = null;
        if (!cCon.hasCommit(commitId)) {
            HashMap<String, Commit> commits = cCon.getCommits();
            boolean found = false;
            outerloop:
            for (String com : commits.keySet()) {
                Commit curr = commits.get(com);
                String id = curr.getId();
                for (int i = 0; i < commitId.length(); i++) {
                    if (id.charAt(i) != commitId.charAt(i)) {
                        break;
                    }
                    if (i == commitId.length() - 1) {
                        found = true;
                        currCommit = curr;
                        break outerloop;
                    }
                }
            }
            if (!found) {
                System.out.println("No commit with that id exists.");
                return;
            }
        }
        if (currCommit == null) {
            currCommit = cCon.getCommit(commitId);
        }
        if (!currCommit.hasFile(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File currFile = currCommit.getFile(fileName);
        Path currPath = currFile.toPath();
        Path copiedPath = Paths.get(userDir, fileName);
        try {
            Files.copy(currPath, copiedPath,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Could not copy the file.");
            return;
        }
        bCon.serialize();
    }

    /** Second case of check out.
     * @param arg1 argument 1
     * @param currBranch current branch
     * @param currCommit current commit*/
    public static void executeThree(String arg1, Branch currBranch,
                                    Commit currCommit) {
        String branchName = arg1;
        if (!branchExists(branchName)) {
            return;
        }
        Branch checkOut = bCon.getBranch(branchName);
        if (!checkOut(checkOut, currBranch)) {
            return;
        }
        HashMap<String, File> add = currStage.getAdd();
        HashSet<String> remove = currStage.getRemove();
        if (!add.isEmpty() || !remove.isEmpty()) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it or add it first.");
            return;
        }
        HashMap<String, File> currBlob = currCommit.getBlob();
        File userDire = new File(userDir);
        HashSet<String> untracked = new HashSet<>();
        for (File curr : userDire.listFiles()) {
            String name = curr.getName();
            if (!ignore.contains(name)) {
                if (!currBlob.containsKey(name)) {
                    untracked.add(name);
                }
            }
        }
        if (!untracked.isEmpty()) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it or add it first.");
            return;
        }
        for (String key : currBlob.keySet()) {
            File currFile = currBlob.get(key);
            String fileName = currFile.getName();
            String dir = userDir + "/" + fileName;
            File dirFile = new File(dir);
            dirFile.delete();
        }
        currCommit = checkOut.getHead();
        HashMap<String, File> blob = currCommit.getBlob();
        for (String key : blob.keySet()) {
            File currFile  = blob.get(key);
            Path currPath = currFile.toPath();
            Path copiedPath = Paths.get(userDir, key);
            try {
                Files.copy(currPath, copiedPath,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("Could not copy the file.");
                return;
            }
        }
        bCon.setCurrent(checkOut.getName());
        bCon.serialize();
    }

    /** Checks if the branch exists.
     * @param branchName name of the branch
     * @return if branch exists or not*/
    public static boolean branchExists(String branchName) {
        if (!bCon.hasBranch(branchName)) {
            System.out.println("No such branch exists.");
            return false;
        }
        return true;
    }

    /** Checks if checkout is needed.
     * @param check branch checking out
     * @param currBranch current branch
     * @return if checkout is needed or not*/
    public static boolean checkOut(Branch check, Branch currBranch) {
        if (check.equals(currBranch)) {
            System.out.println("No need to checkout the current branch.");
            return false;
        }
        return true;
    }
}
