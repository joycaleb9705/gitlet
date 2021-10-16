package gitlet;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** Command for merging branches.
 * @author Sun Hyuk Ahn*/
public class MergeCommand {

    /** Current directory.*/
    private static String userDir = System.getProperty("user.dir");
    /** Branch control.*/
    private static BranchControl bCon = Gitlet.getBCon();
    /** All the branches.*/
    private static HashMap<String, Branch> branches = bCon.getBranches();
    /** The current stage.*/
    private static Staging currStage = bCon.getCurrStage();

    /** Gets the list of untracked files.
     * @param ignore files we ignore
     * @param currBlob files in our blob
     *
     * @return untracked files*/
    public static HashSet<String> untrack(HashSet<String> ignore,
                                          HashMap<String, File> currBlob) {
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
        return untracked;
    }

    /** @param untracked untracked files
     * @param branchName name of the branch
     * @param add staged files
     * @param currBranch current branch
     * @param remove files to remove
     * @return true or false*/
    public static boolean initialExecute(HashSet<String> untracked,
                                         String branchName,
                                         HashMap<String, File> add,
                                         HashSet<String> remove,
                                         Branch currBranch) {
        if (!add.isEmpty() || !remove.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return true;
        }
        if (!untracked.isEmpty()) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it or add it first.");
            return true;
        }
        if (!branches.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return true;
        }
        if (branchName.equals(currBranch.getName())) {
            System.out.println("Cannot merge a branch with itself.");
            return true;
        }
        return false;
    }

    /** Executes the command.
     * @param branchName name of the branch*/
    public static void execute(String branchName) {
        Branch currBranch = bCon.getCurrBranch();
        HashMap<String, File> add = currStage.getAdd();
        HashSet<String> remove = currStage.getRemove();
        Commit currCom = currBranch.getHead();
        HashMap<String, File> currBlob = currCom.getBlob();
        HashSet<String> ignore = bCon.getIgnore();
        HashSet<String> untracked = untrack(ignore, currBlob);
        Branch branch = branches.get(branchName);
        if (initialExecute(untracked, branchName, add, remove, currBranch)) {
            return;
        }
        Commit mergeCom = branch.getHead();
        Commit splitCom = getSplit(mergeCom, branch, currCom, currBranch);
        String currId = currCom.getId();
        String mergeId = mergeCom.getId();
        if (splitCom.getId().equals(mergeId)) {
            System.out.println(
                    "Given branch is an ancestor of the current branch.");
            return;
        }
        if (splitCom.getId().equals(currId)) {
            CheckOutCommand.execute(branchName, "", "");
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        boolean conflict = false;
        HashSet<String> files = getFiles(splitCom, currCom, mergeCom);
        for (String key : files) {
            File splitFile = splitCom.getFile(key);
            File currFile = currCom.getFile(key);
            File mergeFile = mergeCom.getFile(key);
            if (equals(splitFile, currFile) && !equals(splitFile, mergeFile)
                    && mergeFile != null) {
                CheckOutCommand.execute(mergeCom.getId(),
                        "--", mergeFile.getName());
                add.put(mergeFile.getName(), mergeFile);
                bCon.serialize();
            } else if (equals(splitFile, currFile) && mergeFile == null) {
                RemoveCommand.execute(key);
                remove.add(key);
                bCon.serialize();
            } else if (equals(splitFile, mergeFile)
                    || equals(currFile, mergeFile)) {
                continue;
            } else {
                conflict = true;
                mergeConflict(key, currFile, mergeFile, add);
            }
        }
        mergeCommit(conflict, branchName, currBranch, currId, mergeId);
    }

    /** Sets the split commit.
     * @param mergeCom the merge commit
     * @param branch the merge branch
     * @param currCom the current commit
     * @param currBranch the curent branch
     * @return split commit*/
    public static Commit getSplit(Commit mergeCom,
                                Branch branch, Commit currCom,
                                Branch currBranch) {
        Commit splitCom = null;
        HashSet<String> prevCom = new HashSet<String>();
        while (mergeCom != null) {
            prevCom.add(mergeCom.getId());
            mergeCom = mergeCom.getParent();
        }
        mergeCom = branch.getHead();
        while (currCom != null) {
            if (prevCom.contains(currCom.getId())) {
                splitCom = currCom;
                currCom = currBranch.getHead();
                break;
            }
            currCom = currCom.getParent();
        }
        return splitCom;
    }

    /** Gets all the files.
     * @param curCom current commit
     * @param splitCom split commit
     * @param mergeCom merge commit
     * @return all the files*/
    public static HashSet<String> getFiles(Commit splitCom, Commit curCom,
                                           Commit mergeCom) {
        HashSet<String> files = new HashSet<String>();
        for (String split : splitCom.getBlob().keySet()) {
            files.add(split);
        }
        for (String curr : curCom.getBlob().keySet()) {
            if (!files.contains(curr)) {
                files.add(curr);
            }
        }
        for (String merge : mergeCom.getBlob().keySet()) {
            if (!files.contains(merge)) {
                files.add(merge);
            }
        }
        return files;
    }

    /** Handles the merge conflict.
     * @param key the current id
     * @param currFile the current file
     * @param mergeFile the merge file
     * @param add staged files*/
    public static void mergeConflict(String key, File currFile,
                                     File mergeFile,
                                     HashMap<String, File> add) {
        File newFile = new File(userDir + "/" + key);
        String currContent = Utils.readContentsAsString(currFile);
        String mergeContent;
        if (mergeFile == null) {
            mergeContent = "";
        } else {
            mergeContent = Utils.readContentsAsString(mergeFile);
        }
        String newContent = "<<<<<<< HEAD\n"
                + currContent + "=======\n" + mergeContent + ">>>>>>>\n";
        Utils.writeContents(newFile, newContent);
        add.put(key, newFile);
        bCon.serialize();
    }

    /** Makes a new commit for the merge.
     * @param conflict to check if there was a conflict or not.
     * @param branchName name of the branch
     * @param branch current branch
     * @param currId id of the current commit
     * @param mergeId id of the merged commit*/
    public static void mergeCommit(boolean conflict, String branchName,
                                   Branch branch, String currId,
                                   String mergeId) {
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
        CommitCommand.execute("Merged " + branchName
                + " into " + branch.getName() + ".", true);
        bCon = Gitlet.getBCon();
        Commit newCommit = bCon.getCurrBranch().getHead();
        String firstId = "";
        String secondId = "";
        for (int i = 0; i < 7; i++) {
            firstId += currId.charAt(i);
            secondId += mergeId.charAt(i);
        }
        newCommit.setMergeId(firstId + " " + secondId);
        bCon.serialize();
    }


    /** Checks if two files are equal.
     * @param f1 first file
     * @param f2 second file
     * @return true if same content, else false*/
    public static boolean sameContent(File f1, File f2) {
        if (f1.exists() && !f2.exists()) {
            return false;
        }
        if (!f1.exists() && f2.exists()) {
            return false;
        }
        if (!f1.exists() && !f2.exists()) {
            return true;
        }

        if (f1.length() != f2.length()) {
            return false;
        }

        byte[] contentOne = Utils.readContents(f1);
        byte[] contentTwo = Utils.readContents(f2);
        return Arrays.equals(contentOne, contentTwo);
    }

    /** @param f1 first file
     * @param f2 second file
     * @return true if equal, else false*/
    public static boolean equals(File f1, File f2) {
        if (f1 != null && f2 != null) {
            return sameContent(f1, f2);
        } else if (f1 == null && f2 != null) {
            return false;
        } else {
            return f1 == null;
        }
    }
}
