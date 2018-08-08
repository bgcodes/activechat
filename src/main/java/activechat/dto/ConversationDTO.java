package activechat.dto;

import activechat.dto.messages.MessageDTO;
import activechat.model.Conversation;
import activechat.model.Message;

import java.util.ArrayList;
import java.util.List;

public class ConversationDTO {

    private Long idConversation;

    private GameDTO game;

    private List<MessageDTO> messages = new ArrayList<>();

    public ConversationDTO(Conversation conversation) {
        idConversation = conversation.getIdConversation();
        if (conversation.getGame() != null) {
            game = new GameDTO(conversation.getGame());
        } else {
            game = null;
        }
        for (Message m: conversation.getMessages()) {
            messages.add(new MessageDTO(m));
        }
    }

    public Long getIdConversation() {
        return idConversation;
    }

    public void setIdConversation(Long idConversation) {
        this.idConversation = idConversation;
    }

    public GameDTO getGame() {
        return game;
    }

    public void setGame(GameDTO game) {
        this.game = game;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}
