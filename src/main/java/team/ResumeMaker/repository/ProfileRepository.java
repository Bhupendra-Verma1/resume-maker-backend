package team.ResumeMaker.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import team.ResumeMaker.document.ProfileDocument;

import java.util.Optional;

@Repository
public interface ProfileRepository extends MongoRepository<ProfileDocument, String> {

    Optional<ProfileDocument> findByEmail(String email);

    ProfileDocument findByClerkId(String clerkId);

    Boolean existsByClerkId(String clerkId);
}