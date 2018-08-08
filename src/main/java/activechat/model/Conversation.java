package activechat.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idConversation")
    private Long idConversation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idGame", referencedColumnName = "idGame")
    private Game game;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "idConversation", referencedColumnName = "idConversation")
    private List<Message> messages = new ArrayList<>();

    public Long getIdConversation() {
        return idConversation;
    }

    public void setIdConversation(Long idConversation) {
        this.idConversation = idConversation;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "Conversation: [" + "idConversation=" + idConversation + ']';
    }

    public String gameToString() {
        return "Game: [id=" + game.getIdGame() + ", <white='" + game.getWhite() + "' VS black='" + game.getBlack() + "'> boardState='" + game.getBoardState() + "']";
    }
}
