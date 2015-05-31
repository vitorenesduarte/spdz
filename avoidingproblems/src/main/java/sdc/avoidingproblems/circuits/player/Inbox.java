package sdc.avoidingproblems.circuits.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import sdc.avoidingproblems.circuits.message.Commit;
import sdc.avoidingproblems.circuits.message.Message;
import sdc.avoidingproblems.circuits.message.MultiplicationShare;
import sdc.avoidingproblems.circuits.message.Open;
import sdc.avoidingproblems.circuits.message.OpenCommited;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Inbox {

    private Integer numberOfOtherPlayers;
    private final Map<Long, List<MultiplicationShare>> multShares;
    private final List<Open> openList;
    private final List<Commit> commitList;
    private final List<OpenCommited> openCommitedList;
    private final Semaphore LOCK;
    private final Semaphore MULT_DONE;
    private final Semaphore OPEN_DONE;
    private final Semaphore COMMIT_DONE;
    private final Semaphore OPEN_COMMITED_DONE;

    public Inbox() {
        multShares = new HashMap();
        openList = new ArrayList();
        commitList = new ArrayList();
        openCommitedList = new ArrayList();
        LOCK = new Semaphore(1);
        MULT_DONE = new Semaphore(0);
        OPEN_DONE = new Semaphore(0);
        COMMIT_DONE = new Semaphore(0);
        OPEN_COMMITED_DONE = new Semaphore(0);
    }

    public void setNumberOfOtherPlayer(Integer numberOfOtherPlayers) {
        this.numberOfOtherPlayers = numberOfOtherPlayers;
    }

    public void addMessage(Message message) throws InterruptedException {
        LOCK.acquire();
        if (message instanceof MultiplicationShare) {
            MultiplicationShare mShare = (MultiplicationShare) message;
            Long multID = mShare.getMultID();
            if (multShares.containsKey(multID)) {
                multShares.get(multID).add(mShare);
            } else {
                List<MultiplicationShare> list = new ArrayList();
                list.add(mShare);
                multShares.put(multID, list);
            }
            if (multShares.get(multID).size() == numberOfOtherPlayers) {
                MULT_DONE.release();
            }
        } else if (message instanceof Open) {
            openList.add((Open) message);
            if (openList.size() == numberOfOtherPlayers) {
                OPEN_DONE.release();
            }
        } else if (message instanceof Commit) {
            commitList.add((Commit) message);
            if (commitList.size() == numberOfOtherPlayers) {
                COMMIT_DONE.release();
            }
        } else if (message instanceof OpenCommited) {
            openCommitedList.add((OpenCommited) message);
            if (openCommitedList.size() == (numberOfOtherPlayers + 1) * numberOfOtherPlayers) {
                OPEN_COMMITED_DONE.release();
            }
        } else {
            System.out.println("MESSAGE NOT SUPPORTED YET");
        }
        LOCK.release();
    }

    public List<MultiplicationShare> waitForMultiplicationShares(Long multiplicationImWaitingFor) throws InterruptedException {
        MULT_DONE.acquire();
        LOCK.acquire();
        List<MultiplicationShare> result = new ArrayList(multShares.get(multiplicationImWaitingFor));
        multShares.remove(multiplicationImWaitingFor);
        LOCK.release();

        return result;
    }

    public List<Open> waitForOpen() throws InterruptedException {
        OPEN_DONE.acquire();
        LOCK.acquire();
        List<Open> result = new ArrayList(openList);
        openList.clear();
        LOCK.release();

        return result;
    }

    public List<Commit> waitForCommit() throws InterruptedException {
        COMMIT_DONE.acquire();
        LOCK.acquire();
        List<Commit> result = new ArrayList(commitList);
        commitList.clear();
        LOCK.release();

        return result;
    }

    public List<OpenCommited> waitForOpenCommited() throws InterruptedException {
        OPEN_COMMITED_DONE.acquire();
        LOCK.acquire();
        List<OpenCommited> result = new ArrayList(openCommitedList);
        openCommitedList.clear();
        LOCK.release();

        return result;
    }
}
