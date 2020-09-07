package i4jlive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.utils.IGUtils;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;

public class SerializeUtil {

    public static IGClient getClientFromSerialize(File serializedClient, File serializedCookies) throws ClassNotFoundException, IOException {
        InputStream fileIn = new FileInputStream(serializedClient);
        OkHttpClient httpClient = formHttpClient(deserialize(serializedCookies, SerializableCookieJar.class));
        
        IGClient client = IGClient.from(fileIn, httpClient);
        fileIn.close();

        return client;
    }

    @SneakyThrows
    public static void serialize(Object o, File to) {
        FileOutputStream file = new FileOutputStream(to);
        ObjectOutputStream out = new ObjectOutputStream(file);

        out.writeObject(o);
        out.close();
        file.close();
    }

    @SneakyThrows
    public static <T> T deserialize(File file, Class<T> clazz) {
        InputStream in = new FileInputStream(file);
        ObjectInputStream oIn = new ObjectInputStream(in);

        T t = clazz.cast(oIn.readObject());

        in.close();
        oIn.close();

        return t;
    }

    public static OkHttpClient formHttpClient(SerializableCookieJar jar) {
        return IGUtils.defaultHttpClientBuilder().cookieJar(jar).build();
    }
}
