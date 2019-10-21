/**
 * 
 */
package com.seeyon.ctp.organization.principal;


/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 *
 * 2010-11-15
 */
public class NoSuchPrincipalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5904171803653821980L;
	
	public NoSuchPrincipalException() {
		super();
	}

	public NoSuchPrincipalException(String message) {
		super(message);
	}
}
