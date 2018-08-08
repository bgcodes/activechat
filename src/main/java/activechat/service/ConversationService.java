package activechat.service;

import activechat.dto.form.NewGameFormDTO;
import activechat.dto.messages.GameTurnDTO;
import activechat.dto.messages.MessageDTO;
import activechat.model.Conversation;
import activechat.model.Game;
import activechat.model.Message;
import activechat.model.Participation;
import activechat.repository.ConversationRepository;
import activechat.repository.GameRepository;
import activechat.repository.MessageRepository;
import activechat.repository.ParticipationRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("conversationService")
public class ConversationService {

    private ParticipationRepository participationRepository;

    private ConversationRepository conversationRepository;

    private GameRepository gameRepository;

    private MessageRepository messageRepository;

    public ConversationService(@Qualifier("participationRepository") ParticipationRepository participationRepository,
                               @Qualifier("conversationRepository") ConversationRepository conversationRepository,
                               @Qualifier("gameRepository") GameRepository gameRepository,
                               @Qualifier("messageRepository") MessageRepository messageRepository) {

        this.participationRepository = participationRepository;
        this.conversationRepository = conversationRepository;
        this.gameRepository = gameRepository;
        this.messageRepository = messageRepository;
    }


    public Optional<Conversation> findOneByIdConversation(Long idConversation) {
        return conversationRepository.findByIdConversation(idConversation);
    }

    public List<Participation> findAllConversationCompanions(String participant) {
        return participationRepository.findAllConversationCompanions(participant);
    }

    public Optional<Participation> findByConversationCompanion(String user, String companion) {
        return participationRepository.findByConversationCompanion(user, companion);
    }

    public void createConversation(String user1, String user2) {
        Conversation conversation = conversationRepository.save(new Conversation());

        Participation part1 = new Participation();
        part1.setConversation(conversation);
        part1.setParticipant(user1);
        participationRepository.save(part1);

        Participation part2 = new Participation();
        part2.setConversation(conversation);
        part2.setParticipant(user2);
        participationRepository.save(part2);
    }

    public List<Message> findAllByIdConversation(Long idConversation) {
        return messageRepository.findAllByIdConversation(idConversation);
    }

    public void createMessage(MessageDTO messageDTO, Long idConversation) {
        messageRepository.save(messageDTO.toMessage(idConversation));
    }

    public Optional<Game> findOneByIdGame(Long idGame) {
        return gameRepository.findByIdGame(idGame);
    }

    public Game createGame(NewGameFormDTO newGameFormDTO) {
        Game game = new Game();
        game.setWhite(newGameFormDTO.getWhite());
        game.setBlack(newGameFormDTO.getBlack());
        game.setCurrentTurn(1);
        game.setBoardState("bbbbbbbbbbbb--------wwwwwwwwwwww");
        Game savedGame = gameRepository.save(game);

        // save idGame in conversation entity
        conversationRepository.updateConversationGame(newGameFormDTO.getIdConversation(), savedGame.getIdGame());

        return savedGame;
    }

    public void updateGameState(GameTurnDTO gameTurn) {
        gameRepository.updateGameState(gameTurn.getGameId(), gameTurn.getTurn(), gameTurn.getBoardState());
    }

}
