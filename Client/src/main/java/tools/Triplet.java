package tools;

import java.util.concurrent.LinkedBlockingQueue;

public class Triplet {
    public final String regex;
    public final LinkedBlockingQueue<String> messages;
    public final Object lock;

    public Triplet(String regex) {
        this.lock = new Object();
        this.regex = regex;
        this.messages = new LinkedBlockingQueue<>();
    }
}
