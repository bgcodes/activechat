package activechat.service;

import activechat.dto.messages.GameTurnDTO;
import activechat.model.Archive;
import activechat.model.Game;
import activechat.repository.ArchiveRepository;
import activechat.repository.ConversationRepository;
import activechat.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("archiveService")
public class ArchiveService {

    private ArchiveRepository archiveRepository;

    private ConversationRepository conversationRepository;

    private GameRepository gameRepository;

    @Autowired
    public ArchiveService(@Qualifier("archiveRepository") ArchiveRepository archiveRepository,
                          @Qualifier("conversationRepository") ConversationRepository conversationRepository,
                          @Qualifier("gameRepository") GameRepository gameRepository) {

        this.archiveRepository = archiveRepository;
        this.conversationRepository = conversationRepository;
        this.gameRepository = gameRepository;
    }

    public List<Archive> findAllByUsername(String username) {
        return archiveRepository.findAllByWhiteOrBlack(username, username);
    }

    public Archive createArchive(Game game, GameTurnDTO gameTurn, Long idConversation) {
        Archive archive = new Archive();
        archive.setIdConversation(idConversation);
        archive.setWhite(game.getWhite());
        archive.setBlack(game.getBlack());
        archive.setResult(gameTurn.getGameOver());
        archive.setStartDate(game.getStartDate());
        archive.setEndDate(new Date(gameTurn.getSendingDate()));

        conversationRepository.updateConversationGame(idConversation, null);

        gameRepository.deleteByIdGame(game.getIdGame());

        return archiveRepository.save(archive);
    }

}
