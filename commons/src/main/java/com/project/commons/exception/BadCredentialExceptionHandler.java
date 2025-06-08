package com.project.commons.exception;

import com.project.commons.model.enums.MethodMessage;
import com.project.commons.model.response.ObjectApiResponse;
import com.project.commons.model.response.ObjectMessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by user on Jul, 2024
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class BadCredentialExceptionHandler extends BadCredentialsException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BadCredentialExceptionHandler(String message) {
		super(message);
	}
	
	public BadCredentialExceptionHandler(String message, Throwable throwable) {
		super(message, throwable);

		ObjectMessageResponse msg = new ObjectMessageResponse();
		msg.setMessage(throwable.getMessage());
		ObjectApiResponse dto = new ObjectApiResponse();
		dto.setStatus(MethodMessage.MSG_USER_BAD_CREDENTIAL.getStatus().getEn());
		dto.setMessage(MethodMessage.MSG_USER_BAD_CREDENTIAL.getEn());
		dto.setData(msg);
		new ResponseEntity<>(dto, HttpStatus.OK);
	}

}
