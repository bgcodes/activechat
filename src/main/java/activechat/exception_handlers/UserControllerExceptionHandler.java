package activechat.exception_handlers;

import activechat.controller.UserController;
import activechat.dto.ErrorMessageDTO;
import activechat.exceptions.RegisterException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(assignableTypes = UserController.class)
public class UserControllerExceptionHandler {

    private final Log log = LogFactory.getLog(getClass());

    @ExceptionHandler(RegisterException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDTO handleRegisterException(RegisterException e) {
        log.warn(e.getMessage());
        return new ErrorMessageDTO(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn(e.getMessage());

//        // <
//        BindingResult result = e.getBindingResult();
//        List<FieldError> fieldErrors = result.getFieldErrors();
//        for (org.springframework.validation.FieldError fieldError: fieldErrors) {
//            log.warn("MethodArgumentNotValidException: " + fieldError.getField() + ": " + fieldError.getDefaultMessage());
//        }
//        // />

        return new ErrorMessageDTO("userData.error.validationError");
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessageDTO handleUnknownException(Exception e) {
        return new ErrorMessageDTO("userService.error.unknownError");
    }

}
