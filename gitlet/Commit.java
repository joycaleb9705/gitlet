package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Date;
import java.util.Collection;

/** The commit object.
 * @author Sun Hyuk Ahn*/
public class Commit implements Serializable {
    /** Message.*/
    private String message;
    /** Id.*/
    private String id;
    /** Date.*/
    private String date;
    /** Parent.*/
    private Commit parent;
    /** Blob.*/
    private HashMap<String, File> blob;
    /** Merged.*/
    private boolean merge;
    /** MergeId.*/
    private String mergeId;
    /** ShortId.*/
    private String shortId;

    /** Commit constructor.
     * @param msg message
     * @param par parent
     * @param blobs blob
     * @param merged merge
     */
    public Commit(String msg, Commit par,
                  HashMap<String, File> blobs, boolean merged) {
        if (msg == null || msg.equals("")) {
            throw new IllegalArgumentException(
                    "Please enter a commit message.");
        }
        this.message = msg;
        if (message.equals("initial commit")) {
            this.date = "Wed Dec 31 16:00:00 1969 -0800";
        } else {
            DateFormat dateFormat =
                    new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
            this.date = dateFormat.format(new Date()) + " -0800";
        }
        this.parent = par;
        this.blob = blobs;
        this.id = Utils.sha1(message, date);
        merge = merged;
        shortId = "";
        for (int i = 0; i < 6; i++) {
            shortId += this.id.charAt(i);
        }
    }

    /** @return message*/
    public String getMessage() {
        return message;
    }

    /** @return id*/
    public String getId() {
        return id;
    }

    /** @return parent*/
    public Commit getParent() {
        return parent;
    }

    /** @return date*/
    public String getDate() {
        return date;
    }

    /** @return  blob*/
    public HashMap<String, File> getBlob() {
        return blob;
    }

    /** @param name name of the file.
     * @return has file or not*/
    public boolean hasFile(String name) {
        if (blob == null) {
            return false;
        }
        return blob.containsKey(name);
    }

    /** @return all the files.*/
    public Collection<File> getFiles() {
        if (blob == null) {
            return null;
        }
        return blob.values();
    }

    /** @param fileName name of the file
     * @return file*/
    public File getFile(String fileName) {
        return blob.get(fileName);
    }

    /** @param b blob*/
    public void setBlob(HashMap<String, File> b) {
        this.blob = b;
    }

    /** @param m merged or not.*/
    public void setMerge(boolean m) {
        this.merge = m;
    }

    /** @return isMerge*/
    public boolean isMerge() {
        return merge;
    }

    /** @param mid merge id*/
    public void setMergeId(String mid) {
        mergeId = mid;
    }

    /** @return mergeId*/
    public String getMergeId() {
        return mergeId;
    }

    /** @return shortId*/
    public String getShortId() {
        return shortId;
    }
}
