package chat;

import java.awt.Font;

public class Constants {
    public static final String MCAST_GROUP = "230.1.1.1";
    public static final int MCAST_PORT = 4000;
    public static final int BUF_LEN = 8192;
    public static final int TTL = 4;
    public static Font getFont(int size) 
    {
        return new Font("Segoe UI Emoji", Font.PLAIN, size); 
    }    
    public static Font getFont() 
    {
        return new Font("Segoe UI Emoji", Font.PLAIN, 12); 
    }    
}
