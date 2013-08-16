package com.raweng.built;

import java.util.HashMap;

/**
 *To retrieve information related to network call failure.
 *
 * @author  raw engineering, Inc
 */
public class BuiltError {

	private int errorCode;
	private String errorMessage;
	private HashMap<String, Object> error;

	/**
	 * Returns error code.
	 * @return int value.
	 */
	public int errorCode() {
		return errorCode;
	}

	/**
	 * Set error code.
	 * 
	 * @param errorCode
	 * 						error code provided by built.io server.
	 */
	public void errorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * 
	 * Returns error in string format.
	 */
	public String errorMessage() {
		return errorMessage;
	}

	/**
	 * Set error messages.
	 * 
	 * @param errorMessage
	 * 						error message.
	 * 			
	 */
	public void errorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * 
	 * Returns error in {@linkplain HashMap} format where error is key and its respective information as HashMap&#39;s value.
	 */
	public HashMap<String, Object> errors() {
		return error;
	}

	/**
	 * Set errors
	 * 
	 * @param error
	 * 				{@link HashMap} object. 
	 */
	public void errors(HashMap<String, Object> error) {
		this.error = error;
	}
}
