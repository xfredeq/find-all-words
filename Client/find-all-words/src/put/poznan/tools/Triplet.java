package put.poznan.tools;

import java.sql.PreparedStatement;
import java.util.concurrent.LinkedBlockingQueue;

public class Triplet {
    public String regex;
    public LinkedBlockingQueue<String> messages;
    public final Object lock;

    public Triplet(String regex) {
        this.lock = new Object();
        this.regex = regex;
        this.messages = new LinkedBlockingQueue<>();
    }
}
