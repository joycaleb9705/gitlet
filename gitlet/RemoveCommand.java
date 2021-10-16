package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

/** Command for removing a file.
 *  @author Sun Hyuk Ahn
 */
public class RemoveCommand {

    /** Current directory.*/
    private static String userDir = System.getProperty("user.dir");

    /** Executes the remove command.
     * @param fileName name of the file*/
    public static void execute(String fileName) {
        BranchControl bCon = Gitlet.getBCon();
        Branch currBranch = bCon.getCurrBranch();
        Commit currCom = currBranch.getHead();
        Staging stage = bCon.getCurrStage();
        HashMap<String, File> add = stage.getAdd();
        HashSet<String> remove = stage.getRemove();
        if (add.containsKey(fileName)) {
            add.remove(fileName);
            bCon.serialize();
            return;
        }
        if (currCom.hasFile(fileName)) {
            File removing = new File(userDir + "/" + fileName);
            Utils.restrictedDelete(removing);
            stage.getRemoved().add(fileName);
            remove.add(fileName);
            bCon.serialize();
            return;
        }
        System.out.println("No reason to remove the file.");
    }
}
