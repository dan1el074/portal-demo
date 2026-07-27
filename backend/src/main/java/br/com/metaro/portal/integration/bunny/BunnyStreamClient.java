package br.com.metaro.portal.integration.bunny;

import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.integration.bunny.dto.TusCredentialsDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Component
public class BunnyStreamClient {
    public static final String TUS_UPLOAD_ENDPOINT = "https://video.bunnycdn.com/tusupload";
    private static final String API_BASE_URL = "https://video.bunnycdn.com/library";

    private final BunnyStreamProperties properties;
    private final RestTemplate restTemplate;

    public BunnyStreamClient(BunnyStreamProperties properties) {
        this.properties = properties;
        this.restTemplate = new RestTemplate();
    }

    public String createVideo(String title) {
        validateConfiguration();
        HttpHeaders headers = createAuthenticationHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request =
                new HttpEntity<>(Map.of("title", title), headers);
        String url = API_BASE_URL + "/" + properties.getLibraryId() + "/videos";
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getBody() == null || response.getBody().get("guid") == null) {
            throw new UnprocessableEntityException(
                    "Não foi possível criar o vídeo no Bunny Stream!"
            );
        }

        return response.getBody().get("guid").toString();
    }

    public void deleteVideo(String providerVideoId) {
        validateConfiguration();
        HttpEntity<Void> request = new HttpEntity<>(createAuthenticationHeaders());
        String url = API_BASE_URL + "/" + properties.getLibraryId()
                + "/videos/" + providerVideoId;

        try {
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
        } catch (HttpClientErrorException.NotFound ignored) {
            // O registro remoto já foi removido; a exclusão local pode continuar.
        }
    }

    public TusCredentialsDto generateTusCredentials(String providerVideoId) {
        validateConfiguration();
        long expiration = Instant.now().plus(1, ChronoUnit.HOURS).getEpochSecond();
        String content = properties.getLibraryId()
                + properties.getApiKey()
                + expiration
                + providerVideoId;

        return new TusCredentialsDto(sha256Hex(content), expiration);
    }

    public String buildPlaybackUrl(String providerVideoId) {
        return removeTrailingSlash(properties.getEmbedBaseUrl())
                + "/" + properties.getLibraryId()
                + "/" + providerVideoId;
    }

    public String getLibraryId() {
        return properties.getLibraryId();
    }

    private HttpHeaders createAuthenticationHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("AccessKey", properties.getApiKey());
        return headers;
    }

    private void validateConfiguration() {
        if (!StringUtils.hasText(properties.getApiKey())
                || !StringUtils.hasText(properties.getLibraryId())) {
            throw new UnprocessableEntityException(
                    "A integração com o Bunny Stream não está configurada!"
            );
        }
    }

    private String removeTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 não está disponível", exception);
        }
    }

}
