package team.ResumeMaker.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;



@Component
public class MongoConfigDebug {

    @PostConstruct
    public void debug() {
        String uri = System.getenv("MONGODB_URI");

        System.out.println("========== MONGO DEBUG ==========");
        System.out.println("MONGODB_URI exists: " + (uri != null));
        System.out.println("MONGODB_URI length: " + (uri != null ? uri.length() : 0));

        if (uri != null) {
            System.out.println("MONGODB_URI starts with: " +
                    uri.substring(0, Math.min(20, uri.length())));
        }

        System.out.println("=================================");
    }
}