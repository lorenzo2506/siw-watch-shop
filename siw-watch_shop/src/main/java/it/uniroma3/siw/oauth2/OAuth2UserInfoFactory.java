package it.uniroma3.siw.oauth2;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
        if ("google".equalsIgnoreCase(provider)) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        // In futuro:
        // if ("github".equalsIgnoreCase(provider)) return new GithubOAuth2UserInfo(attributes);
        // if ("linkedin".equalsIgnoreCase(provider)) return new LinkedInOAuth2UserInfo(attributes);
        throw new IllegalArgumentException("Unsupported OAuth2 provider: " + provider);
    }
}

