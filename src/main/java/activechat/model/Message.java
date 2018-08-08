package activechat.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "idMessage")
    private Long idMessage;

    @Column(name = "idConversation")
    private Long idConversation;

    @Column(name = "author")
    private String author;

    @Column(name = "content")
    private String content;

    @Column(name = "sendingDate")
    private Date sendingDate;

    public Long getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(Long idMessage) {
        this.idMessage = idMessage;
    }

    public Long getIdConversation() {
        return idConversation;
    }

    public void setIdConversation(Long idConversation) {
        this.idConversation = idConversation;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(Date sendingDate) {
        this.sendingDate = sendingDate;
    }

    @Override
    public String toString() {
        return "Message{" +
                "idMessage=" + idMessage +
                ", idConversation=" + idConversation +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", sendingDate=" + sendingDate +
                '}';
    }
}
