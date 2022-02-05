package put.poznan.tools;

import java.sql.PreparedStatement;

public class Triplet {
    public String regex;
    public String response;
    public final Object lock;

    public Triplet(String regex) {
        this.lock = new Object();
        this.regex = regex;
        this.response = null;
    }
}
