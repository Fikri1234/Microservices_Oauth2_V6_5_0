/**
 * 
 */
package com.project.commons.model.response;

import lombok.Data;

/**
 * @author Fikri
 *
 */
@Data
public class ObjectApiResponse {
	private String status;
	private String message;
	private Object data;

}
