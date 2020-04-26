package com.github.steveice10.mc.auth.test;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.auth.service.ProfileService;
import com.github.steveice10.mc.auth.service.SessionService;

import java.net.Proxy;
import java.util.UUID;

public class MinecraftAuthTest {
    private static final String USERNAME = "Username";
    private static final String EMAIL = "Username@mail.com";
    private static final String PASSWORD = "Password";
    private static final String ACCESS_TOKEN = null;
    private static final boolean REQUIRE_SECURE_TEXTURES = true;

    private static final Proxy PROXY = Proxy.NO_PROXY;

    public static void main(String[] args) {
        profileLookup();
        auth();
    }

    private static void profileLookup() {
        ProfileService repository = new ProfileService(PROXY);
        repository.findProfilesByName(new String[] {USERNAME}, new ProfileService.ProfileLookupCallback() {
            @Override
            public void onProfileLookupSucceeded(GameProfile profile) {
                System.out.println("Found profile: " + profile);
            }

            @Override
            public void onProfileLookupFailed(GameProfile profile, Exception e) {
                System.out.println("Lookup for profile " + profile.getName() + " failed!");
                e.printStackTrace();
            }
        });
    }

    private static void auth() {
        String clientToken = UUID.randomUUID().toString();
        AuthenticationService auth = new AuthenticationService(clientToken, PROXY);
        auth.setUsername(EMAIL);
        if(ACCESS_TOKEN != null) {
            auth.setAccessToken(ACCESS_TOKEN);
        } else {
            auth.setPassword(PASSWORD);
        }

        try {
            auth.login();
        } catch(RequestException e) {
            System.err.println("Failed to log in!");
            e.printStackTrace();
            return;
        }

        SessionService service = new SessionService();
        for(GameProfile profile : auth.getAvailableProfiles()) {
            try {
                service.fillProfileProperties(profile);

                System.out.println("Profile: " + profile);
                System.out.println("Profile Textures: " + profile.getTextures(REQUIRE_SECURE_TEXTURES));
            } catch(Exception e) {
                System.err.println("Failed to get properties and textures of profile " + profile + ".");
                e.printStackTrace();
            }
        }
    }
}
