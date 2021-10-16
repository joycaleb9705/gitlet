package gitlet;

import java.util.HashMap;

/** Command for removing a branch.
 *  @author Sun Hyuk Ahn
 */
public class RemoveBranchCommand {

    /** Executes the command .
     * @param branchName name of the branch*/
    public static void execute(String branchName) {
        BranchControl bCon = Gitlet.getBCon();
        HashMap<String, Branch> branches = bCon.getBranches();
        if (!branches.containsKey(branchName)) {
            System.out.println(" A branch with that name does not exist.");
            return;
        }
        Branch currBranch = bCon.getCurrBranch();
        if (currBranch.getName().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        branches.remove(branchName);
        bCon.serialize();
    }
}
