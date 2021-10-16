package gitlet;

import java.util.HashMap;

/** Command for displaying the global log.
 * @author Sun Hyuk Ahn*/
public class GlobalLogCommand {

    /** Executes the command.*/
    public static void execute() {
        CommitControl comCon = Gitlet.getCCon();
        HashMap<String, Commit> commits = comCon.getCommits();
        for (String key : commits.keySet()) {
            Commit currCom = commits.get(key);
            System.out.println("===");
            System.out.println("commit " + currCom.getId());
            System.out.println("Date: " + currCom.getDate());
            System.out.println(currCom.getMessage());
            System.out.println();
        }
    }
}
