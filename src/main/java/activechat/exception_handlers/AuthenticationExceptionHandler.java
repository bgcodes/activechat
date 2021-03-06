package activechat.exception_handlers;

import activechat.dto.ErrorMessageDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AuthenticationExceptionHandler {

  private final Log log = LogFactory.getLog(getClass());

  @ExceptionHandler(AuthenticationException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorMessageDTO handleAuthenticationException(AuthenticationException e) {
    log.warn(e.getMessage());
    return new ErrorMessageDTO("login.error.badLogin");
  }

}
