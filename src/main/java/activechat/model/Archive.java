package activechat.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "archive")
public class Archive {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "idGame")
    private Long idGame;

    @Column(name = "idConversation")
    private Long idConversation;

    @Column(name = "white")
    private String white;

    @Column(name = "black")
    private String black;

    @Column(name = "result")
    private Integer result;

    @Column(name = "startDate")
    private Date startDate;

    @Column(name = "endDate")
    private Date endDate;

    public Long getIdGame() {
        return idGame;
    }

    public void setIdGame(Long idGame) {
        this.idGame = idGame;
    }

    public Long getIdConversation() {
        return idConversation;
    }

    public void setIdConversation(Long idConversation) {
        this.idConversation = idConversation;
    }

    public String getWhite() {
        return white;
    }

    public void setWhite(String white) {
        this.white = white;
    }

    public String getBlack() {
        return black;
    }

    public void setBlack(String black) {
        this.black = black;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Archive: [" +
                "idGame=" + idGame +
                ", idConversation=" + idConversation +
                ", white='" + white + '\'' +
                ", black='" + black + '\'' +
                ", result=" + result +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ']';
    }
}
