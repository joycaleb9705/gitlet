package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** Command for staging a new file.
 *  @author Sun Hyuk Ahn
 */
public class AddCommand {

    /** Executes the add command.
     * @param fileName the name of the file getting staged.*/
    public static void execute(String fileName) {
        File newFile = new File(fileName);
        if (!newFile.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        if (newFile.isDirectory()) {
            System.out.println("Cannot add directory.");
            return;
        }
        BranchControl bCon = Gitlet.getBCon();
        Branch currBranch = bCon.getCurrBranch();
        Commit currCommit = currBranch.getHead();
        Staging stage = bCon.getCurrStage();
        HashSet<String> remove = stage.getRemove();
        HashMap<String, File> add = stage.getAdd();
        HashSet<String> removed = stage.getRemoved();
        if (removed.contains(fileName) && remove.contains(fileName)) {
            remove.remove(fileName);
            bCon.serialize();
            return;
        }
        if (currCommit.hasFile(fileName)) {
            try {
                if (sameContent(newFile, currCommit.getFile(fileName))) {
                    return;
                }
            } catch (IOException e) {
                return;
            }
        }

        if (!add.containsKey(fileName)) {
            add.put(fileName, newFile);
            bCon.serialize();
            return;
        }
        if (add.containsKey(fileName)) {
            return;
        }
        if (remove.contains(fileName)) {
            remove.remove(fileName);
            bCon.serialize();
        } else if (!currCommit.hasFile(newFile.getName())) {
            add.put(fileName, newFile);
            bCon.serialize();
        }
    }

    /** Checks if two files have the same content.
     * @param f1 file one
     * @param f2 file two
     * @return true if same, false if not.
     */
    public static boolean sameContent(File f1, File f2) throws IOException {
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
