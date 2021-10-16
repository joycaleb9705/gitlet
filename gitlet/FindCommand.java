package gitlet;

import java.util.HashMap;

/** Command for finding commits.
 * @author Sun Hyuk Ahn*/
public class FindCommand {

    /** Executes the find command.
     * @param message message of the commit*/
    public static void execute(String message) {
        CommitControl comCon = Gitlet.getCCon();
        HashMap<String, Commit> commits = comCon.getCommits();
        int count = 0;
        for (String key : commits.keySet()) {
            Commit currCom = commits.get(key);
            if (currCom.getMessage().equals(message)) {
                System.out.println(currCom.getId());
                count++;
            }
        }
        if (count == 0) {
            System.err.println("Found no commit with that message.");
        }
    }
}
