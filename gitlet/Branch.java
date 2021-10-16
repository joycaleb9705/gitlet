package gitlet;

import java.io.Serializable;

/** Branch object.
 *  @author Sun Hyuk Ahn
 */
public class Branch implements Serializable {
    /** Name of the branch. */
    private String name;
    /** The head (or the parent) of the branch.*/
    private Commit head;

    /** Branch constructor.
     * @param n name of the branch.
     * @param h head of the branch.
     */
    public Branch(String n, Commit h) {
        this.name = n;
        this.head = h;
    }

    /** Returns the head commit.
     * @return head*/
    public Commit getHead() {
        return head;
    }

    /** Returns the name of the branch.
     * @return name of the branch.*/
    public String getName() {
        return name;
    }

    /** Sets the head to a new head.
     * @param newHead a new head commit.*/
    public void setHead(Commit newHead) {
        this.head = newHead;
    }
}
