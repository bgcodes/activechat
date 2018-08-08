package activechat.dto.form;

public class ConversationFormDTO {

    private String invitationSender;

    private String invitationReceiver;

    public String getInvitationSender() {
        return invitationSender;
    }

    public void setInvitationSender(String invitationSender) {
        this.invitationSender = invitationSender;
    }

    public String getInvitationReceiver() {
        return invitationReceiver;
    }

    public void setInvitationReceiver(String invitationReceiver) {
        this.invitationReceiver = invitationReceiver;
    }
}
