package dev.gamified.GamifiedPlatform.repository;

import dev.gamified.GamifiedPlatform.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /*
     Retorna apenas os campos necessários para login e geração de token JWT
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.scopes WHERE u.username = :username AND u.deleted = false")
    Optional<User> findUserByUsername(@Param("username") String username);

    /*
     Retorna apenas os campos necessários para verificação de email
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deleted = false")
    Optional<User> findByEmail(@Param("email") String email);

    /*
     Retorna apenas os campos necessários para o processo de verificação
     */
    @Query("SELECT u FROM User u WHERE u.emailVerificationToken = :token AND u.deleted = false")
    Optional<User> findByEmailVerificationToken(@Param("token") String token);

    /*
     Query otimizada que retorna apenas boolean sem carregar a entidade
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username AND u.deleted = false")
    Boolean existsByUsername(@Param("username") String username);

    /*
     Query otimizada que retorna apenas boolean sem carregar a entidade
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.deleted = false")
    Boolean existsByEmail(@Param("email") String email);

    /*
     Conta total de usuários não deletados
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = false")
    Long countActiveUsers();

}
