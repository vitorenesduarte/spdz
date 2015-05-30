package sdc.avoidingproblems.circuits.player;

import java.util.concurrent.Semaphore;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Outbox {

    private final static int BB_MAX_SIZE = 1024;

    private final String[] boundedBuffer;
    private final int N;
    private final Semaphore items;
    private final Semaphore slots;
    private final Semaphore sGet;
    private final Semaphore sPut;
    private int iGet;
    private int iPut;

    public Outbox() {
        N = BB_MAX_SIZE;
        boundedBuffer = new String[N];
        items = new Semaphore(0);
        slots = new Semaphore(N);
        sGet = new Semaphore(1);
        sPut = new Semaphore(1);
        iGet = 0;
        iPut = 0;
    }

    public String get() throws InterruptedException {
        items.acquire();
        sGet.acquire();

        String output = boundedBuffer[iGet];

        boundedBuffer[iGet] = null;
        iGet = (iGet + 1) % N;

        sGet.release();
        slots.release();

        return output;
    }

    public void put(String input) throws InterruptedException {
        slots.acquire();
        sPut.acquire();

        boundedBuffer[iPut] = input;
        iPut = (this.iPut + 1) % this.N;

        sPut.release();
        items.release();
    }
}
