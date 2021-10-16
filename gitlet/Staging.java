package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

/** Object controlling the staged files.
 * @author Sun Hyuk Ahn*/
public class Staging implements Serializable {
    /** Files to be added.*/
    private HashMap<String, File> add;
    /** Files to be removed.*/
    private HashSet<String> remove;
    /** Files already removed.*/
    private HashSet<String> removed;

    /** Stage constructor.*/
    public Staging() {
        add = new HashMap<String, File>();
        remove = new HashSet<String>();
        removed = new HashSet<String>();
    }

    /** @return all the added*/
    public HashMap<String, File> getAdd() {
        return add;
    }

    /** @return all the remove*/
    public HashSet<String> getRemove() {
        return remove;
    }

    /** @return the removed*/
    public HashSet<String> getRemoved() {
        return removed;
    }
}
