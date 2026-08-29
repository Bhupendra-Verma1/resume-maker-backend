package team.ResumeMaker.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClient mongoClient() {
        String mongoUri = System.getenv("MONGODB_URI");

        if (mongoUri == null || mongoUri.isBlank()) {
            throw new IllegalStateException(
                    "MONGODB_URI environment variable is not configured"
            );
        }

        return MongoClients.create(mongoUri);
    }
}