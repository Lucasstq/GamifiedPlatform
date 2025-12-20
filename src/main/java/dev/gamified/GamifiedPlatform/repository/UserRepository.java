package dev.gamified.GamifiedPlatform.repository;

import dev.gamified.GamifiedPlatform.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
