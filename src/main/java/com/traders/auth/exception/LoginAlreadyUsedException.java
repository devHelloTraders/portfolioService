package com.traders.auth.exception;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class LoginAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public LoginAlreadyUsedException() {
        super( "Login name already used!", "userManagement", "userexists");
    }
}
