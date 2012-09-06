package org.ucp.ugat.core.exceptions;

public class AuthFailedException extends UGATException {
	public static final long serialVersionUID = 1;
	
	public AuthFailedException() {
		setMessageStr("Utilizador inválido ou sem permissões.");
	}
}
