package com.kuaiba.site.core.exception;

/**
 * 读取数据异常
 * 
 * @author larry.qi
 *
 */
public class ReadException extends SecurityException {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Create an exception with an error code that maps to {@link ErrorIDs} and
	 * message text.
	 */
	public ReadException(String msg) {
		super(ErrorIDs.READ_FAILIED, msg);
	}

	/**
	 * Create exception with error id, message and related exception.
	 */
	public ReadException(String msg, Exception e) {
		super(ErrorIDs.READ_FAILIED, msg, e);
	}
}
