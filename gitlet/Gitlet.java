package gitlet;

import java.io.IOException;

/** Head control of the Gitlet.
 * @author Sun Hyuk Ahn*/
public class Gitlet {

    /** Returns the BranchControl.
     * @return branch control*/
    public static BranchControl getBCon() {
        try {
            String userDir = System.getProperty("user.dir");
            BranchControl bCon = BranchControl.deserialize(userDir
                    + "/.gitlet/BranchControlSerialized");
            return bCon;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Not in an initialized Gitlet directory.");
            return null;
        }
    }

    /** Returns the CommitControl.
     * @return commit control*/
    public static CommitControl getCCon() {
        try {
            String userDir = System.getProperty("user.dir");
            CommitControl cCon = CommitControl.deserialize(userDir
                    + "/.gitlet/CommitControlSerialized");
            return cCon;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Not in an initialized Gitlet directory.");
            return null;
        }
    }
}
