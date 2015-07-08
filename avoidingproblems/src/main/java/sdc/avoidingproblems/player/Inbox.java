package sdc.avoidingproblems.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdc.avoidingproblems.message.Commit;
import sdc.avoidingproblems.message.Message;
import sdc.avoidingproblems.message.MultiplicationShare;
import sdc.avoidingproblems.message.Open;
import sdc.avoidingproblems.message.OpenCommited;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Inbox {

    private static final Logger logger = Logger.getLogger(Inbox.class.getName());
    private static final int TIMEOUT = 20;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

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
    
    public Integer getNumberOfOtherPlayers(){
        return numberOfOtherPlayers;
    }

    public void setNumberOfOtherPlayers(Integer numberOfOtherPlayers) {
        this.numberOfOtherPlayers = numberOfOtherPlayers;
    }

    public void addMessage(Message message) throws InterruptedException {
        lock();
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
            logger.warning("MESSAGE NOT SUPPORTED");
        }
        unlock();
    }

    public List<MultiplicationShare> waitForMultiplicationShares(Long multiplicationImWaitingFor) throws InterruptedException {
        for (int count = 1; !MULT_DONE.tryAcquire(TIMEOUT, TIME_UNIT); count++) {
            logger.log(Level.INFO, "waitForMultiplicationShares timeout {0}", count);
        }
        lock();
        List<MultiplicationShare> result = new ArrayList(multShares.get(multiplicationImWaitingFor));
        multShares.remove(multiplicationImWaitingFor);
        unlock();

        return result;
    }

    public List<Open> waitForOpen() throws InterruptedException {
        for (int count = 1; !OPEN_DONE.tryAcquire(TIMEOUT, TIME_UNIT); count++) {
            logger.log(Level.INFO, "waitForOpen timeout {0}", count);
        }
        lock();
        List<Open> result = new ArrayList(openList);
        openList.clear();
        unlock();

        return result;
    }

    public List<Commit> waitForCommit() throws InterruptedException {
        for (int count = 1; !COMMIT_DONE.tryAcquire(TIMEOUT, TIME_UNIT); count++) {
            logger.log(Level.INFO, "waitForCommit timeout {0}", count);
        }
        lock();
        List<Commit> result = new ArrayList(commitList);
        commitList.clear();
        unlock();

        return result;
    }

    public List<OpenCommited> waitForOpenCommited() throws InterruptedException {
        for (int count = 1; !OPEN_COMMITED_DONE.tryAcquire(TIMEOUT, TIME_UNIT); count++) {
            logger.log(Level.INFO, "waitForOpenCommited timeout {0}", count);
        }
        lock();
        List<OpenCommited> result = new ArrayList(openCommitedList);
        openCommitedList.clear();
        unlock();

        return result;
    }

    private void lock() throws InterruptedException {
        for (int count = 1; !LOCK.tryAcquire(TIMEOUT, TIME_UNIT); count++) {
            logger.log(Level.INFO, "lock timeout {0}", count);
        }
    }

    private void unlock() {
        LOCK.release();
    }
}
