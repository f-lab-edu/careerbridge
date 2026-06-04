package com.careerbridge.global.security;

import com.careerbridge.global.exception.UnauthorizedAccessException;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserRole;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

    private final String secretKey;
    private final long accessTokenValidityInMilliseconds;
    private final ObjectMapper objectMapper;

    public JwtTokenProvider(
            @Value("${jwt.secret:careerbridge-local-secret-key-must-be-at-least-32-bytes}") String secretKey,
            @Value("${jwt.access-token-validity-ms:3600000}") long accessTokenValidityInMilliseconds,
            ObjectMapper objectMapper
    ) {
        this.secretKey = secretKey;
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.objectMapper = objectMapper;
    }

    public String createAccessToken(User user) {
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", user.getEmail());
        payload.put("role", user.getRole().name());
        payload.put("exp", Instant.now().toEpochMilli() + accessTokenValidityInMilliseconds);

        String encodedHeader = encodeJson(header);
        String encodedPayload = encodeJson(payload);
        String signatureTarget = encodedHeader + "." + encodedPayload;

        return signatureTarget + "." + sign(signatureTarget);
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = splitToken(token);
            String signatureTarget = parts[0] + "." + parts[1];
            if (!sign(signatureTarget).equals(parts[2])) {
                return false;
            }

            Number expiration = (Number) readPayload(token).get("exp");
            return expiration.longValue() > Instant.now().toEpochMilli();
        } catch (RuntimeException exception) {
            return false;
        }
    }

    public String extractEmail(String token) {
        if (!validateToken(token)) {
            throw new UnauthorizedAccessException();
        }
        return (String) readPayload(token).get("sub");
    }

    public UserRole extractRole(String token) {
        if (!validateToken(token)) {
            throw new UnauthorizedAccessException();
        }
        return UserRole.valueOf((String) readPayload(token).get("role"));
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            return BASE64_URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (Exception exception) {
            throw new IllegalStateException("Could not create JWT.", exception);
        }
    }

    private Map<String, Object> readPayload(String token) {
        try {
            String[] parts = splitToken(token);
            byte[] payloadBytes = BASE64_URL_DECODER.decode(parts[1]);
            return objectMapper.readValue(payloadBytes, new TypeReference<>() {
            });
        } catch (Exception exception) {
            throw new UnauthorizedAccessException();
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(keySpec);
            return BASE64_URL_ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Could not sign JWT.", exception);
        }
    }

    private String[] splitToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new UnauthorizedAccessException();
        }
        return parts;
    }
}
