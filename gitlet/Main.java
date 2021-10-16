package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Sun Hyuk Ahn
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String[] args) {
        String currDir = System.getProperty("user.dir");
        try {
            switch (args[0]) {
            case "init":
                try {
                    InitCommand.execute(currDir);
                } catch (IOException e) {
                    System.out.println("A Gitlet version-control system "
                            + "already exists in the current directory.");
                }
                break;
            case "add":
                AddCommand.execute(args[1]);
                break;
            case "commit":
                if (args.length < 2) {
                    System.out.println("Please enter a commit message.");
                    return;
                }
                CommitCommand.execute(args[1], false);
                break;
            case "rm":
                RemoveCommand.execute(args[1]);
                break;
            case "log":
                LogCommand.execute();
                break;
            case "global-log":
                GlobalLogCommand.execute();
                break;
            case "find":
                FindCommand.execute(args[1]);
                break;
            case "status":
                StatusCommand.execute();
                break;
            case "checkout":
                checkOut(args);
                break;
            case "branch":
                BranchCommand.execute(args[1]);
                break;
            case "rm-branch":
                RemoveBranchCommand.execute(args[1]);
                break;
            case "reset":
                ResetCommand.execute(args[1]);
                break;
            case "merge":
                MergeCommand.execute(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Please enter a command.");
        }
    }

    /** Takes care of checkout command.
     * @param args input argument*/
    public static void checkOut(String[] args) {
        if (args.length == 2) {
            CheckOutCommand.execute(args[1], "", "");
        } else if (args.length == 3) {
            if (!args[1].equals("--")) {
                System.out.println("Incorrect operands.");
                return;
            }
            CheckOutCommand.execute("--", args[2], "");
        } else {
            if (!args[2].equals("--")) {
                System.out.println("Incorrect operands.");
                return;
            }
            CheckOutCommand.execute(args[1], "--", args[3]);
        }
    }
}
