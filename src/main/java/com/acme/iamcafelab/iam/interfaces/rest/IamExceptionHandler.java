package com.acme.iamcafelab.iam.interfaces.rest;

import com.acme.iamcafelab.iam.domain.exceptions.SignInFailedException;
import com.acme.iamcafelab.iam.domain.exceptions.SignUpFailedException;
import com.acme.iamcafelab.shared.interfaces.rest.resources.MessageResource;
import com.acme.iamcafelab.shared.interfaces.rest.support.CafeLabScopedExceptionHandlerSupport;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.acme.iamcafelab.iam.interfaces.rest")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class IamExceptionHandler extends CafeLabScopedExceptionHandlerSupport {

    @ExceptionHandler(SignInFailedException.class)
    public ResponseEntity<MessageResource> handleSignInFailed(SignInFailedException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResource(ex.getMessage()));
    }

    @ExceptionHandler(SignUpFailedException.class)
    public ResponseEntity<MessageResource> handleSignUpFailed(SignUpFailedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResource(ex.getMessage()));
    }
}