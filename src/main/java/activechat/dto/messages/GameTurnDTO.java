package activechat.dto.messages;

public class GameTurnDTO {

    private Long gameId;

    private Integer turn;

    private Integer gameOver;

    private String boardState;

    private Long sendingDate;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public Integer getGameOver() {
        return gameOver;
    }

    public void setGameOver(Integer gameOver) {
        this.gameOver = gameOver;
    }

    public String getBoardState() {
        return boardState;
    }

    public void setBoardState(String boardState) {
        this.boardState = boardState;
    }

    public Long getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(Long sendingDate) {
        this.sendingDate = sendingDate;
    }

    @Override
    public String toString() {
        return "GameTurnDTO: [" +
                "gameId=" + gameId +
                ", turn=" + turn +
                ", gameOver=" + gameOver +
                ", boardState='" + boardState + '\'' +
                ", sendingDate=" + sendingDate +
                ']';
    }
}
