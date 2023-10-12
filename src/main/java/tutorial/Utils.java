package tutorial;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {
    public static String readFile(String filePath) {
        String str;
        try {
            ClassLoader classLoader = Utils.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(filePath);

            str = new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error reading file [" + filePath + "]", e);
        }
        return str;
    }
}