package i4jlive;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.utils.IGUtils;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {
    public static void main(String args[]) throws IGLoginException {
        // BasicConfigurator.configure(); // configure basic logging output
        Pair<String, String> pair = args.length >= 2 ? new Pair<>(args[0], args[1]) : new Pair<>("saved session", "");
        IGClient client = login(pair.getFirst(), pair.getSecond());
        
        new Application(client).start();
        
        System.exit(0);
    }
    
    private final IGClient client;
    
    public Application(IGClient client) {
        this.client = client;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Saving session...");
                SerializeUtil.serialize(client.getHttpClient().cookieJar(), new File("igcookies.ser"));
                SerializeUtil.serialize(client, new File("igclient.ser"));
                System.out.println("Saved!");
            }
        });
    }
    
    public void start() {
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.print("Input a command (Type 'quit' to quit): ");
        while (!(input = scanner.nextLine()).equalsIgnoreCase("quit")) {
            switch(input) {
                case "live":
                    new LiveBroadcastProcess(client, scanner).start();
                    break;
                default:
                    System.out.println("Unknown command. Available: live");
                    break;
            }
            
            System.out.print("Input a command (Type 'quit' to quit): ");
        }
        
    }
    
    public static IGClient login(String username, String password) throws IGLoginException {
        System.out.printf("Logging into %s\n", username);
        IGClient client = getLoggedInIGClient(username, password);
        log.info("Serializing IGClient and cookies");
        System.out.printf("Logged into %s\n", client.getSelfProfile().getUsername());
        
        // save session
        SerializeUtil.serialize(client.getHttpClient().cookieJar(), new File("igcookies.ser"));
        SerializeUtil.serialize(client, new File("igclient.ser"));
        
        return client;
    }
    
    public static boolean validSerializedLogin(IGClient client) {
        // return true if any requests are successful
        // return false if any request are not (CompletionException arises due to invalid login)
        return CompletableFuture.anyOf(client.actions().simulate().postLoginFlow().toArray(new CompletableFuture[15]))
                .handle((o, tr) -> tr == null)
                .join();
    }
    
    public static IGClient getLoggedInIGClient(String username, String password) throws IGLoginException {
        File serializedClient = new File("igclient.ser"),
             serializedCookies = new File("igcookies.ser");
        
        if (serializedClient.exists() && serializedCookies.exists()) {
            log.info("Found existing serialized info.");
            try {
                IGClient deserialized_client = SerializeUtil.getClientFromSerialize(serializedClient, serializedCookies);
                
                if (validSerializedLogin(deserialized_client)) {
                    System.out.println("Logged into saved session.");
                    return deserialized_client;
                } else {
                    System.out.println("Invalid saved session.");
                }
            } catch (Exception e) {}
        }
        
        System.out.println("Creating a new IGClient");
        
        return IGClient.builder()
        .username(username)
        .password(password)
        .client(IGUtils.defaultHttpClientBuilder().cookieJar(new SerializableCookieJar()).build())
        .simulatedLogin();
    }
}
