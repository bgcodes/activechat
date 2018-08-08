package activechat.controller;

import activechat.dto.form.UserFormDTO;
import activechat.exceptions.RegisterException;
import activechat.model.User;
import activechat.service.ArchiveService;
import activechat.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class UserController {

    private ArchiveService archiveService;

    private UserService userService;

    private final Log log = LogFactory.getLog(getClass());


    @Autowired
    public UserController(@Qualifier("archiveService") ArchiveService archiveService,
                          @Qualifier("userService") UserService userService) {

        this.archiveService = archiveService;
        this.userService = userService;
    }


    @RequestMapping(method=POST, path="/app/register")
    @ResponseBody
    public ResponseEntity<HttpStatus> createUser(@Valid @RequestBody UserFormDTO userFormDTO) throws RegisterException {
        long start = System.nanoTime();
        Optional<User> getByUsernameOrEmail = userService.findOneByUsernameOrEmail(userFormDTO.getUsername(), userFormDTO.getEmail());
        getByUsernameOrEmail.ifPresent(
                user -> {
                    if (getByUsernameOrEmail.get().getUsername().equals(userFormDTO.getUsername()))
                        throw new RegisterException("register.error.usernameExists");
                    if (getByUsernameOrEmail.get().getEmail().equals(userFormDTO.getEmail()))
                        throw new RegisterException("register.error.emailExists");
                }
        );
        userService.createUser(userFormDTO);

        log.info(format("%s: %.10f [s]", "createUser", ((System.nanoTime() - start)/Math.pow(10,9))));
        return ResponseEntity.ok(HttpStatus.OK);
    }


}
