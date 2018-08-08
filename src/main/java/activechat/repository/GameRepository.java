package activechat.repository;

import activechat.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository("gameRepository")
public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByIdGame(Long idGame);

    @Transactional
    @Modifying
    @Query(value = "UPDATE games g SET g.currentTurn = :turn, g.boardState = :boardState WHERE g.idGame = :idGame", nativeQuery = true)
    int updateGameState(@Param("idGame") Long idGame, @Param("turn") Integer turn, @Param("boardState") String boardState);

    @Transactional
    void deleteByIdGame(Long idGame);

}
