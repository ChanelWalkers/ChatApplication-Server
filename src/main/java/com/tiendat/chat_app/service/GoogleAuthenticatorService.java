package com.tiendat.chat_app.service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Component;

@Component
public class GoogleAuthenticatorService {

    private final GoogleAuthenticator gAuth;

    public GoogleAuthenticatorService() {
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setWindowSize(5)
                .build();

        this.gAuth = new GoogleAuthenticator(config);
    }

    public GoogleAuthenticatorKey createCredentials() {
        return gAuth.createCredentials();
    }

    public String getKey(GoogleAuthenticatorKey googleAuthenticatorKey) {
        return googleAuthenticatorKey.getKey();
    }

    public String getQRBarUrl(String email, GoogleAuthenticatorKey key) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("Chat Application", email, key);
    }

    public boolean verifyCode(String secret, int otp) {
        return gAuth.authorize(secret, otp);
    }
}
