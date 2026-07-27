package br.com.metaro.portal.util.video;

import br.com.metaro.portal.integration.bunny.BunnyStreamClient;
import br.com.metaro.portal.util.video.dto.VideoUploadDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final BunnyStreamClient bunnyStreamClient;

    public VideoService(
            VideoRepository videoRepository,
            BunnyStreamClient bunnyStreamClient
    ) {
        this.videoRepository = videoRepository;
        this.bunnyStreamClient = bunnyStreamClient;
    }

    @Transactional
    public Video createPendingVideo(String name) {
        String providerVideoId = bunnyStreamClient.createVideo(name);

        Video video = new Video();
        video.setName(name);
        video.setProvider(VideoProvider.BUNNY);
        video.setProviderVideoId(providerVideoId);
        video.setPlaybackUrl(bunnyStreamClient.buildPlaybackUrl(providerVideoId));
        video.setStatus(VideoStatus.PENDING);

        return videoRepository.save(video);
    }

    public VideoUploadDto createUploadInstructions(Video video) {
        BunnyStreamClient.TusCredentials credentials =
                bunnyStreamClient.generateTusCredentials(video.getProviderVideoId());

        return new VideoUploadDto(
                video.getId(),
                video.getProviderVideoId(),
                bunnyStreamClient.getLibraryId(),
                BunnyStreamClient.TUS_UPLOAD_ENDPOINT,
                credentials.signature(),
                credentials.expiration(),
                video.getPlaybackUrl()
        );
    }

    @Transactional
    public void markAsReady(Video video) {
        video.setStatus(VideoStatus.READY);
        videoRepository.save(video);
    }

    @Transactional
    public void delete(Video video) {
        if (video.getProvider() == VideoProvider.BUNNY) {
            bunnyStreamClient.deleteVideo(video.getProviderVideoId());
        }
        videoRepository.delete(video);
    }
}
