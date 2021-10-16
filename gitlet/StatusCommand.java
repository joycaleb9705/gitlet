package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** Command for printing the status.
 *  @author Sun Hyuk Ahn
 */
public class StatusCommand {

    /** Current directory.*/
    private static String userDir = System.getProperty("user.dir");
    /** Current branch control.*/
    private static BranchControl bCon = Gitlet.getBCon();

    /** Executes the status command.*/
    public static void execute() {
        if (bCon == null) {
            return;
        }
        Staging currStage = bCon.getCurrStage();
        HashMap<String, Branch> branches = bCon.getBranches();
        Branch currBranch = bCon.getCurrBranch();
        System.out.println("=== Branches ===");
        System.out.println("*" + currBranch.getName());
        for (String key : branches.keySet()) {
            if (!key.equals(currBranch.getName())) {
                Branch branch = branches.get(key);
                System.out.println(branch.getName());
            }
        }
        System.out.println();

        HashMap<String, File> add = currStage.getAdd();
        HashSet<String> remove = currStage.getRemove();
        System.out.println("=== Staged Files ===");
        for (String key : add.keySet()) {
            File curr = add.get(key);
            System.out.println(curr.getName());
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String rem : remove) {
            System.out.println(rem);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        modified();
        removed();
        System.out.println();

        System.out.println("=== Untracked Files ===");
        untracked();
        System.out.println();
    }

    /** Prints out the modified files not staged.*/
    public static void modified() {
        HashSet<String> ignore = bCon.getIgnore();
        Commit currCommit = bCon.getCurrBranch().getHead();
        HashMap<String, File> currBlob = currCommit.getBlob();
        File userDire = new File(userDir);
        HashSet<String> modified = new HashSet<>();
        for (File curr : userDire.listFiles()) {
            String name = curr.getName();
            if (!ignore.contains(name)) {
                if (currBlob.containsKey(name)) {
                    File blobFile = currBlob.get(name);
                    if (!sameContent(curr, blobFile)) {
                        modified.add(name);
                    }
                }
            }
        }
        for (String name : modified) {
            System.out.println(name + " (modified)");
        }
    }

    /** Prints out the removed files not staged.*/
    public static void removed() {
        Staging currStage = bCon.getCurrStage();
        File userDire = new File(userDir);
        ArrayList<String> dir = new ArrayList<String>();
        HashSet<String> remove = currStage.getRemove();
        for (File file : userDire.listFiles()) {
            dir.add(file.getName());
        }
        Commit currCommit = bCon.getCurrBranch().getHead();
        HashMap<String, File> currBlob = currCommit.getBlob();
        HashSet<String> deleted = new HashSet<>();
        for (String fileName : currBlob.keySet()) {
            if (!dir.contains(fileName)) {
                if (!remove.contains(fileName)) {
                    deleted.add(fileName);
                }
            }
        }
        for (String name : deleted) {
            System.out.println(name + " (deleted)");
        }
    }

    /** Prints out the untracked files.*/
    public static void untracked() {
        Staging currStage = bCon.getCurrStage();
        HashSet<String> ignore = bCon.getIgnore();
        Commit currCommit = bCon.getCurrBranch().getHead();
        HashMap<String, File> blob = currCommit.getBlob();
        HashMap<String, File> add = currStage.getAdd();
        File userDire = new File(userDir);
        HashSet<String> untracked = new HashSet<>();
        for (File curr : userDire.listFiles()) {
            String name = curr.getName();
            if (!ignore.contains(name)) {
                if (!blob.containsKey(name)) {
                    if (!add.containsKey(name)) {
                        untracked.add(name);
                    }
                }
            }
        }
        for (String name : untracked) {
            System.out.println(name);
        }
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
}
