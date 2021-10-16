package gitlet;

import java.util.HashMap;

/** Command for creating a new branch.
 *  @author Sun Hyuk Ahn
 */
public class BranchCommand {

    /** Executes the brach command.
     * @param branchName the name of the branch getting created.*/
    public static void execute(String branchName) {
        BranchControl bCon = Gitlet.getBCon();
        HashMap<String, Branch> branches = bCon.getBranches();
        if (branches.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        Branch currBranch = bCon.getCurrBranch();
        Branch newBranch = new Branch(branchName, currBranch.getHead());
        branches.put(branchName, newBranch);
        bCon.serialize();
    }
}
