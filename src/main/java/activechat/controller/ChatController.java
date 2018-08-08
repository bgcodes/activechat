package activechat.controller;

import activechat.dto.ConversationDTO;
import activechat.dto.ParticipationDTO;
import activechat.dto.StatsDTO;
import activechat.dto.UserSessionDTO;
import activechat.dto.form.ConversationFormDTO;
import activechat.dto.form.NewGameFormDTO;
import activechat.dto.messages.GameTurnDTO;
import activechat.dto.messages.MessageDTO;
import activechat.exceptions.ChatException;
import activechat.model.Conversation;
import activechat.model.Game;
import activechat.model.Message;
import activechat.model.Participation;
import activechat.service.ArchiveService;
import activechat.service.ConversationService;
import activechat.service.StatsService;
import activechat.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class ChatController {

    private ArchiveService archiveService;

    private UserService userService;

    private ConversationService conversationService;

    private StatsService statsService;

    @Autowired
    public ChatController(@Qualifier("archiveService") ArchiveService archiveService,
                          @Qualifier("userService") UserService userService,
                          @Qualifier("conversationService") ConversationService conversationService,
                          @Qualifier("statsService") StatsService statsService) {

        this.archiveService = archiveService;
        this.userService = userService;
        this.conversationService = conversationService;
        this.statsService = statsService;
    }

    private final Log log = LogFactory.getLog(getClass());



    @MessageMapping("/chat/{id}")
    @SendTo("/room/{id}")
    public MessageDTO handleMessage(MessageDTO message, @DestinationVariable("id") Long idConversation) {
        System.out.println("ChatController (message): " + message.toString() + ", idConversation: " + idConversation);
        conversationService.createMessage(message, idConversation);
        return message;
    }

    @MessageMapping("/game/{id}")
    @SendTo("/room/{id}")
    public GameTurnDTO handleGameTurn(GameTurnDTO gameTurn, @DestinationVariable("id") Long idConversation) throws ChatException {
        System.out.println("ChatController (game): " + gameTurn.toString() + ", idConversation: " + idConversation);
        if (gameTurn.getGameOver() != null) {
            Game game = conversationService.findOneByIdGame(gameTurn.getGameId()).orElseThrow(
                    () -> new ChatException("handleGameTurn.error.gameNotFound"));
            archiveService.createArchive(game, gameTurn, idConversation);
        } else {
            conversationService.updateGameState(gameTurn);
        }
        return gameTurn;
    }

    @RequestMapping(method=GET, path="/app/participations")
    @ResponseBody
    public List<ParticipationDTO> getParticipation(HttpSession session) {
        long start = System.nanoTime();

        UserSessionDTO userSessionDTO = (UserSessionDTO) session.getAttribute("user");

        List<Participation> participations = conversationService.findAllConversationCompanions(userSessionDTO.getUsername());
        List<ParticipationDTO> conversations = new ArrayList<>();

        for (Participation participation: participations) {
            conversations.add(new ParticipationDTO(participation));
        }

        log.info(format("%s: %.10f [s]", "getParticipation", ((System.nanoTime() - start)/Math.pow(10,9))));
        return conversations;
    }

    @RequestMapping(method=GET, path="/app/messages/{id}")
    @ResponseBody
    public List<MessageDTO> getMessages(@PathVariable("id") Long idConversation) {
        long start = System.nanoTime();

        List<Message> messages = conversationService.findAllByIdConversation(idConversation);
        List<MessageDTO> messageDTOS = new ArrayList<>();

        for (Message message: messages) {
            messageDTOS.add(new MessageDTO(message));
        }

        log.info(format("%s: %.10f [s]", "getMessages", ((System.nanoTime() - start)/Math.pow(10,9))));
        return messageDTOS;
    }

    @RequestMapping(method=GET, path="/app/conversation/{id}")
    @ResponseBody
    public ConversationDTO getConversation(@PathVariable("id") Long idConversation) throws Exception {
        long start = System.nanoTime();

        Conversation conversation = conversationService.findOneByIdConversation(idConversation).orElseThrow(
                () -> new Exception("Conversation ["+idConversation+"] not found"));

        ConversationDTO conversationDTO = new ConversationDTO(conversation);

        log.info(format("%s: %.10f [s]", "getMessages", ((System.nanoTime() - start)/Math.pow(10,9))));
        return conversationDTO;
    }

    @RequestMapping(method=POST, path="/app/conversation")
    @ResponseBody
    public ResponseEntity<HttpStatus> createConversation(@Valid @RequestBody ConversationFormDTO conversationDTO, HttpSession session) throws ChatException {
        long start = System.nanoTime();

        UserSessionDTO userSession = (UserSessionDTO) session.getAttribute("user");
        if (!userSession.getUsername().equals(conversationDTO.getInvitationSender()))
            throw new ChatException("createConversation.error.unauthorised");
        if (userSession.getUsername().equals(conversationDTO.getInvitationReceiver()))
            throw new ChatException("createConversation.error.senderEqualsReceiver");

        userService.findOneByUsername(conversationDTO.getInvitationReceiver()).orElseThrow(
                () -> new ChatException("createConversation.error.companionNotFound"));
        conversationService.findByConversationCompanion(conversationDTO.getInvitationSender(), conversationDTO.getInvitationReceiver()).ifPresent(
                participation -> { throw new ChatException("createConversation.error.conversationExists"); });

        conversationService.createConversation(conversationDTO.getInvitationSender(), conversationDTO.getInvitationReceiver());

        log.info(format("%s: %.10f [s]", "createConversation", ((System.nanoTime() - start)/Math.pow(10,9))));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(method=POST, path="/app/newGame")
    @ResponseBody
    public ResponseEntity<HttpStatus> createGame(@Valid @RequestBody NewGameFormDTO newGameFormDTO) throws ChatException {
        long start = System.nanoTime();

        conversationService.findOneByIdConversation(newGameFormDTO.getIdConversation()).ifPresent(conversation -> {
            if (conversation.getGame() == null) {
                conversationService.createGame(newGameFormDTO);
            } else {
                throw new ChatException("createGame.error.gameExists");
            }
        });

        log.info(format("%s: %.10f [s]", "createGame", ((System.nanoTime() - start)/Math.pow(10,9))));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(method=GET, path="/app/stats/{id}")
    @ResponseBody
    public StatsDTO getStats(@PathVariable("id") Long idConversation) {
        long start = System.nanoTime();

        StatsDTO stats = new StatsDTO();
        stats.setMessageStats(statsService.findMessageStats(idConversation));
        stats.setArchiveStats(statsService.findArchiveStats(idConversation));

        log.info(format("%s: %.10f [s]", "getStats", ((System.nanoTime() - start)/Math.pow(10,9))));
        return stats;
    }

}
