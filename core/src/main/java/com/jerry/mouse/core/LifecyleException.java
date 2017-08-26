package com.jerry.mouse.core;

/**
 * 生命周期执行过程中的异常，当抛出该异常时，生命周期将被终止
 * @author xiecr002
 *
 */
public class LifecyleException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6363285916576180598L;

	public LifecyleException() {
		// TODO Auto-generated constructor stub
	}

	public LifecyleException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public LifecyleException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public LifecyleException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}



}
