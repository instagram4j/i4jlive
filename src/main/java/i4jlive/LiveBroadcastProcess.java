package i4jlive;

import java.util.Scanner;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.requests.live.LiveCreateRequest;
import com.github.instagram4j.instagram4j.requests.live.LiveEndBroadcastRequest;
import com.github.instagram4j.instagram4j.requests.live.LiveStartRequest;
import com.github.instagram4j.instagram4j.responses.live.LiveCreateResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LiveBroadcastProcess {
    private IGClient client;
    private Scanner scanner;
    
    public void start() {
        String input;
        LiveCreateResponse response = new LiveCreateRequest().execute(client).join();
        System.out.printf("Broadcast ID: %s\nBroadcast URL (OBS): %s\nBroadcast Key (OBS): %s\n", response.getBroadcast_id(), response.getBroadcastUrl(), response.getBroadcastKey());
        System.out.print("Type 'start' to start broadcast: ");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                new LiveEndBroadcastRequest(response.getBroadcast_id()).execute(client).join();
            }
        });
        
        while ((input = scanner.nextLine()) != null) {
            switch(input) {
                case "start":
                    new LiveStartRequest(response.getBroadcast_id(), false).execute(client);
                    break;
                case "quit":
                case "end":
                    new LiveEndBroadcastRequest(response.getBroadcast_id(), false).execute(client);
                    break;
            }
            
            if (input.equalsIgnoreCase("quit")) break;
            System.out.print("Available commands (start, end, quit): ");
        }
    }
}
