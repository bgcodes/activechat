package activechat.repository;

import activechat.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("participationRepository")
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    @Query(value =
            "SELECT * FROM participation " +
            "WHERE participant NOT LIKE :user AND idConversation IN (" +
                    "SELECT idConversation FROM participation WHERE participant LIKE :user);",
            nativeQuery = true)
    List<Participation> findAllConversationCompanions(@Param("user") String user);

    @Query(value =
            "SELECT * FROM participation " +
                    "WHERE participant NOT LIKE :user AND participant LIKE :companion AND idConversation IN (" +
                    "SELECT idConversation FROM participation WHERE participant LIKE :user) " +
                    "LIMIT 1;",
            nativeQuery = true)
    Optional<Participation> findByConversationCompanion(@Param("user") String user, @Param("companion") String companion);

}
