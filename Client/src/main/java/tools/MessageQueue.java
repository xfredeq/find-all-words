package tools;

import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue {
    public final String regex;
    public final LinkedBlockingQueue<String> messages;

    public MessageQueue(String regex) {
        this.regex = regex;
        this.messages = new LinkedBlockingQueue<>();
    }
}
