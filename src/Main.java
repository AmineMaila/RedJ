import java.io.IOException;

import server.RedisServer;

public class Main {
    
    public static void main(String[] args) {
        int port = 5353;

        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ign) {}
        }

        RedisServer server = new RedisServer(port);
        try {
            server.start();
        } catch(IOException e) {
            System.out.println("Failed to start server: " +  e.getMessage());
        } 

    }
}
