package activechat.controller;

import activechat.dto.UserSessionDTO;
import activechat.dto.form.CredentialsFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/app/session")
public class SessionController {

    private AuthenticationManager authenticationManager;

    @Autowired
    public SessionController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @RequestMapping(method=POST)
    public UserSessionDTO login(@RequestBody CredentialsFormDTO credentialsFormDTO, HttpSession httpSession) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(credentialsFormDTO.getUsername(), credentialsFormDTO.getPassword());
        SecurityContextHolder.getContext().setAuthentication(this.authenticationManager.authenticate(authentication));
        UserSessionDTO userSessionDTO = new UserSessionDTO(credentialsFormDTO.getUsername(), httpSession.getId(), true);
        httpSession.setAttribute("user", userSessionDTO);
        return userSessionDTO;
    }

    @RequestMapping(method=GET)
    public UserSessionDTO session(HttpSession session) {
        return (UserSessionDTO) session.getAttribute("user");
    }

    @RequestMapping(method=DELETE)
    public void logout(HttpSession session) {
        session.invalidate();
    }

}
