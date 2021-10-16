package gitlet;

/** Command for displaying the log.
 * @author Sun Hyuk Ahn*/
public class LogCommand {

    /** Executes the command.*/
    public static void execute() {
        BranchControl bCon = Gitlet.getBCon();
        Branch currBranch = bCon.getCurrBranch();
        Commit currCom = currBranch.getHead();
        while (currCom != null) {
            System.out.println("===");
            System.out.println("commit " + currCom.getId());
            if (currCom.isMerge()) {
                System.out.println("Merge: " + currCom.getMergeId());
            }
            System.out.println("Date: " + currCom.getDate());
            System.out.println(currCom.getMessage());
            System.out.println();
            currCom = currCom.getParent();
        }
    }
}
