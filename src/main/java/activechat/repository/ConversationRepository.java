package activechat.repository;

import activechat.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository("conversationRepository")
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByIdConversation(Long idConversation);

    @Transactional
    @Modifying
    @Query(value = "UPDATE conversations c SET c.idGame = :idGame WHERE c.idConversation = :idConversation", nativeQuery = true)
    int updateConversationGame(@Param("idConversation") Long idConversation, @Param("idGame") Long idGame);

}
