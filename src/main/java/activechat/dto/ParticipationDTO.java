package activechat.dto;

import activechat.model.Participation;

public class ParticipationDTO {

    private Long conversationId;

    private String conversationName;

    public ParticipationDTO(Participation participation) {
        this.conversationId = participation.getConversation().getIdConversation();
        this.conversationName = participation.getParticipant();
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }
}
