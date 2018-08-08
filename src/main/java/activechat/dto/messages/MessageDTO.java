package activechat.dto.messages;

import activechat.model.Message;

import java.util.Date;

public class MessageDTO {

    private String author;

    private String content;

    private Long sendingDate;

    public MessageDTO() {
    }

    public MessageDTO(String author, String content, Long sendingDate) {
        this.author = author;
        this.content = content;
        this.sendingDate = sendingDate;
    }

    public MessageDTO(Message message) {
        author = message.getAuthor();
        content = message.getContent();
        sendingDate = message.getSendingDate().toInstant().toEpochMilli();
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

    public Long getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(Long sendingDate) {
        this.sendingDate = sendingDate;
    }

    public Message toMessage(Long idConversation) {
        Message message = new Message();
        message.setIdConversation(idConversation);
        message.setAuthor(author);
        message.setContent(content);
        message.setSendingDate(new Date(sendingDate));
        return message;
    }

    @Override
    public String toString() {
        return "MessageDTO: [" +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", sendingDate=" + sendingDate +
                ']';
    }
}
