package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;

/** Object controlling all the commits.
 * @author Sun Hyuk Ahn*/
public class CommitControl implements Serializable {
    /** Path.*/
    private String path;
    /** All the commits.*/
    private HashMap<String, Commit> commits;
    /** All the messages.*/
    private HashMap<String, HashSet<String>> messages;
    /** Count of the commit.*/
    private int commitCount;

    /** CommitControl constructor.
     * @param p the directory of the CCon.*/
    public CommitControl(String p) {
        this.path = p + "/CommitControlSerialized";
        commits = new HashMap<String, Commit>();
        messages = new HashMap<String, HashSet<String>>();
        HashMap<String, File> blob = new HashMap<String, File>();
        Commit initCommit = new Commit("initial commit", null, blob, false);
        commits.put(initCommit.getId(), initCommit);
        commitCount = 1;

        HashSet<String> ids = new HashSet<String>();
        ids.add(initCommit.getId());
        messages.put(initCommit.getMessage(), ids);
        serialize();
    }

    /** Creates a new commit.
     * @param message msg
     * @param parent commit's parent
     * @param blobs blob
     * @return new commit.*/
    public Commit newCommit(String message, Commit parent,
                            HashMap<String, File> blobs) {
        Commit curr = new Commit(message, parent, blobs, false);
        commitCount++;
        commits.put(curr.getId(), curr);
        if (messages.containsKey(message)) {
            messages.get(message).add(curr.getId());
        } else {
            HashSet<String> ids = new HashSet<String>();
            ids.add(curr.getId());
            messages.put(message, ids);
        }
        serialize();
        return curr;
    }

    /** @return commit count*/
    public int getCommitCount() {
        return commitCount;
    }

    /** @return all the commits*/
    public HashMap<String, Commit> getCommits() {
        return commits;
    }

    /** @param id id of the commit
     * @return has a commit or not*/
    public boolean hasCommit(String id) {
        return commits.containsKey(id);
    }

    /** @param id of the commit
     *  @return commit*/
    public Commit getCommit(String id) {
        if (!commits.containsKey(id)) {
            return null;
        }
        return commits.get(id);
    }

    /** Writes the object.*/
    public void serialize() {
        try {
            FileOutputStream file = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(this);
        } catch (IOException e) {
            System.out.println("Failed to serialize.");
        }
    }

    /** Reads the object.
     * @param path directory
     * @return cCon*/
    public static CommitControl deserialize(String path) throws
            IOException, ClassNotFoundException {
        FileInputStream file = new FileInputStream(path);
        ObjectInputStream in = new ObjectInputStream(file);
        CommitControl con = (CommitControl) in.readObject();
        in.close();
        file.close();
        return con;
    }

}
