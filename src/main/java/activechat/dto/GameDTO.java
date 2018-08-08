package activechat.dto;

import activechat.model.Game;

public class GameDTO {

    private Long idGame;

    private String white;

    private String black;

    private Integer currentTurn;

    private Long startDate;

    private String boardState;

    public GameDTO(Game game) {
        idGame = game.getIdGame();
        white = game.getWhite();
        black = game.getBlack();
        currentTurn = game.getCurrentTurn();
        startDate = game.getStartDate().toInstant().toEpochMilli();
        boardState = game.getBoardState();
    }

    public Long getIdGame() {
        return idGame;
    }

    public void setIdGame(Long idGame) {
        this.idGame = idGame;
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

    public Integer getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(Integer currentTurn) {
        this.currentTurn = currentTurn;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public String getBoardState() {
        return boardState;
    }

    public void setBoardState(String boardState) {
        this.boardState = boardState;
    }
}
