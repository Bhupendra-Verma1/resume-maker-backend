package team.ResumeMaker.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//@Component
public class MongoConfigDebug {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @PostConstruct
    public void debug() {

        System.out.println("========== MONGO DEBUG ==========");

        String envUri = System.getenv("MONGODB_URI");

        System.out.println("Environment variable exists: " + (envUri != null));

        System.out.println("Spring Mongo URI exists: " + (mongoUri != null));

        if (envUri != null) {
            System.out.println("Environment URI starts with: "
                    + envUri.substring(0, Math.min(20, envUri.length())));
        }

        if (mongoUri != null) {
            System.out.println("Spring URI starts with: "
                    + mongoUri.substring(0, Math.min(20, mongoUri.length())));
        }

        System.out.println("=================================");
    }
}