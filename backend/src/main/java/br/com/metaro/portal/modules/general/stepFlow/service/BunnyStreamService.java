package br.com.metaro.portal.modules.general.stepFlow.service;

import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.modules.general.stepFlow.entities.BunnyStreamProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class BunnyStreamService {
    public static final String TUS_ENDPOINT = "https://video.bunnycdn.com/tusupload";
    private static final String API_BASE = "https://video.bunnycdn.com/library";

    @Autowired
    private BunnyStreamProperties properties;

    private final RestTemplate restTemplate = new RestTemplate();

    public String createVideo(String title) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("AccessKey", properties.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("title", title);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        String url = API_BASE + "/" + properties.getLibraryId() + "/videos";
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getBody() == null || response.getBody().get("guid") == null) {
            throw new UnprocessableEntityException("Não foi possível criar o vídeo no Bunny Stream!");
        }

        return (String) response.getBody().get("guid");
    }

    public void deleteVideo(String bunnyVideoId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("AccessKey", properties.getApiKey());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        String url = API_BASE + "/" + properties.getLibraryId() + "/videos/" + bunnyVideoId;
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
        } catch (HttpClientErrorException.NotFound ignored) {
            // já não existe no bunny, segue o fluxo normalmente
        }
    }

    public TusCredentials generateTusCredentials(String bunnyVideoId) {
        long expiration = Instant.now().plus(1, ChronoUnit.HOURS).getEpochSecond();
        String toHash = properties.getLibraryId() + properties.getApiKey() + expiration + bunnyVideoId;
        return new TusCredentials(sha256Hex(toHash), expiration);
    }

    public String buildViewUrl(String bunnyVideoId) {
        return "https://player.mediadelivery.net/embed/" + properties.getLibraryId() + "/" + bunnyVideoId;
    }

    public String getLibraryId() {
        return properties.getLibraryId();
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public record TusCredentials(String signature, long expiration) {}
}
