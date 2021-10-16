package gitlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** Object controlling all the branches.
 *  @author Sun Hyuk Ahn
 */
public class BranchControl implements Serializable {
    /** Path of the branch control.*/
    private String path;
    /** All the branches.*/
    private HashMap<String, Branch> branches;
    /** Current branch.*/
    private Branch currBranch;
    /** Current stage.*/
    private Staging currStage;
    /** Files we ignore.*/
    private HashSet<String> ignore;

    /** BranchControl constructor.
     * @param p the path of the branch control.
     * @param com the new head commit.*/
    public BranchControl(String p, Commit com) {
        this.path = p + "/BranchControlSerialized";
        this.branches = new HashMap<String, Branch>();
        Branch master = new Branch("master", com);
        branches.put(master.getName(), master);
        currBranch = master;
        currStage = new Staging();
        String[] useless = new String[]{".DS_Store", "out",
            "Makefile", "proj3.iml", "testing",
            ".gitignore", "gitlet", ".gitlet", ".idea"};
        ignore = new HashSet<String>();
        ignore.addAll(Arrays.asList(useless));
        serialize();
    }

    /** @return current stage.*/
    public Staging getCurrStage() {
        return currStage;
    }

    /** @param commit Commits the commit.*/
    public void commit(Commit commit) {
        currBranch.setHead(commit);
        serialize();
    }

    /** Removes the branch.
     * @param branch name*/
    public void remove(String branch) {
        if (!branches.containsKey(branch)) {
            System.out.println("Branch nonexistent.");
            return;
        }
        if (currBranch.getName().equals(branch)) {
            currBranch = null;
        }
        branches.remove(branch);
    }

    /** @param branch name of the branch.
     * @return true if the branch exists else false.*/
    public boolean hasBranch(String branch) {
        return branches.containsKey(branch);
    }

    /** Sets the current branch to branch.
     * @param branch the new branch.*/
    public void setCurrent(String branch) {
        if (!branches.containsKey(branch)) {
            System.out.println("Branch nonexistent.");
            return;
        }
        currBranch = branches.get(branch);
        serialize();
    }

    /** @return ignore files.*/
    public HashSet<String> getIgnore() {
        return ignore;
    }

    /** @return the hash map of the branches.*/
    public HashMap<String, Branch> getBranches() {
        return branches;
    }

    /** @param branch name of the branch.
     * @return returns the branch.*/
    public Branch getBranch(String branch) {
        return branches.get(branch);
    }

    /** @return returns the current branch.*/
    public Branch getCurrBranch() {
        return currBranch;
    }

    /** Writes the content to file.*/
    public void serialize() {
        try {
            FileOutputStream file = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(this);
        } catch (IOException e) {
            System.out.println("Failed to serialize.");
        }
    }

    /** Reads the content to file.
     * @param path of the Branch Control
     * @return Branch Control*/
    public static BranchControl deserialize(String path) throws
            IOException, ClassNotFoundException {
        FileInputStream file = new FileInputStream(path);
        ObjectInputStream in = new ObjectInputStream(file);
        BranchControl bCon = (BranchControl) in.readObject();
        in.close();
        file.close();
        return bCon;
    }
}
