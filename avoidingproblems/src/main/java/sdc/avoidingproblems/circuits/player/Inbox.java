package sdc.avoidingproblems.circuits.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import sdc.avoidingproblems.circuits.message.Message;
import sdc.avoidingproblems.circuits.message.MultiplicationShare;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Inbox {

    private final PlayerInfo playerInfo;
    private final Map<Long, List<MultiplicationShare>> inbox;
    private final Semaphore DONE;
    private final Semaphore LOCK;
    private Integer numberOfShares;

    public Inbox(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
        numberOfShares = -1;
        inbox = new HashMap();
        DONE = new Semaphore(0);
        LOCK = new Semaphore(1);
    }

    public List<MultiplicationShare> waitForMultiplicationShares(Long multiplicationImWaitingFor, Integer numberOfShares) throws InterruptedException {
        LOCK.acquire();
        this.numberOfShares = numberOfShares;
        LOCK.release();
        DONE.acquire();
        LOCK.acquire();
        List<MultiplicationShare> result = new ArrayList(inbox.get(multiplicationImWaitingFor));
        inbox.remove(multiplicationImWaitingFor);
        LOCK.release();
        return result;
    }

    public void addMessage(Message message) throws InterruptedException {
        LOCK.acquire();
        if (message instanceof MultiplicationShare) {
            MultiplicationShare mShare = (MultiplicationShare) message;
            Long multID = mShare.getMultID();
            if (inbox.containsKey(multID)) {
                inbox.get(multID).add(mShare);
                if (inbox.get(multID).size() == numberOfShares) {
                    DONE.release();
                }
            } else {
                List<MultiplicationShare> list = new ArrayList();
                list.add(mShare);
                inbox.put(multID, list);
            }
        } else {
            System.out.println("MESSAGE NOT SUPPORTED YET");
        }
        LOCK.release();
    }

    private void out(String s) {
        System.out.println(playerInfo.getUID() + " !!! " + s);
    }
}
