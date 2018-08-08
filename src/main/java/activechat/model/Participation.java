package activechat.model;

import javax.persistence.*;

@Entity
@Table(name = "participation")
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idParticipation")
    private Long idParticipation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idConversation", referencedColumnName = "idConversation")
    private Conversation conversation;

    @Column(name = "participant")
    private String participant;

    public Long getIdParticipation() {
        return idParticipation;
    }

    public void setIdParticipation(Long idParticipation) {
        this.idParticipation = idParticipation;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    @Override
    public String toString() {
        return "Participation: [" + "idParticipation=" + idParticipation + ", conversation='" + conversation + "', participant='" + participant + "']";
    }
}