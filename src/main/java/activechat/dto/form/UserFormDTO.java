package activechat.dto.form;

import activechat.model.User;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserFormDTO {

    @Pattern(regexp = "^([a-zA-Z0-9]+[-_. ]?)*[a-zA-Z0-9]+$")
    @Size(min=3, max=30)
    private String username;

    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).{6,30}", message = "bad password")
    private String password;

//    @Pattern(regexp = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User toUser(String password, String role) {
        User user = new User();
        user.setUsername(getUsername());
        user.setPassword(password);
        user.setEmail(getEmail());
        user.setRole(role);
        return user;
    }
}
