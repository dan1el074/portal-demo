package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.dto.event.EventDto;
import br.com.metaro.portal.core.dto.info.BirthdayDto;
import br.com.metaro.portal.core.dto.info.HomeInfoDto;
import br.com.metaro.portal.core.repositories.UserRepository;
import br.com.metaro.portal.modules.general.memorando.entities.MemorandoStatus;
import br.com.metaro.portal.modules.general.memorando.repository.MemorandoRepository;
import br.com.metaro.portal.modules.general.post.dto.PostDto;
import br.com.metaro.portal.modules.general.post.repositories.PostRepository;
import br.com.metaro.portal.modules.general.post.repositories.projections.PostProjection;
import br.com.metaro.portal.util.picture.dto.PictureMinDto;
import br.com.metaro.portal.util.smb.files.FileDto;
import br.com.metaro.portal.util.smb.files.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InfoService {
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private EventService eventService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemorandoRepository memorandoRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CacheManager cacheManager;

    @Cacheable("homeInfo")
    @Transactional(readOnly = true)
    public HomeInfoDto getHomeInfo() {
        Long upcomingEvents = eventService.getEventsCount();
        Long openMemorandos = memorandoRepository.countByStatus(MemorandoStatus.PUBLISH);
        List<FileDto> filesDto = fileRepository.findTop3ForHome();

        EventDto eventDto = eventService.getEvent();

        LocalDate today = LocalDate.now();
        List<BirthdayDto> allBirthdays = userRepository.findBirthdaysOfCurrentMonth()
                .stream()
                .map(BirthdayDto::new)
                .toList();

        List<BirthdayDto> todayBirthdaysDto = allBirthdays.stream()
                .filter(dto -> dto.getDay() == today.getDayOfMonth())
                .sorted(Comparator.comparing(BirthdayDto::getDay))
                .toList();
        List<BirthdayDto> monthBirthdaysDto = allBirthdays.stream()
                .filter(dto -> dto.getDay() != today.getDayOfMonth())
                .sorted(Comparator.comparing(BirthdayDto::getDay))
                .toList();

        List<Long> top4Ids = postRepository.findTop4Ids();
        List<PostProjection> rows = postRepository.findFeedByIds(top4Ids);
        Map<Long, List<PostProjection>> grouped = rows.stream()
                .collect(Collectors.groupingBy(PostProjection::getId, LinkedHashMap::new, Collectors.toList()));
        List<PostDto> feedDto = grouped.values().stream()
                .map(postRows -> {
                    PostProjection first = postRows.getFirst();
                    List<PictureMinDto> pictures = postRows.stream()
                            .filter(r -> r.getPictureId() != null)
                            .map(r -> new PictureMinDto(r.getPictureId()))
                            .toList();
                    return new PostDto(first, pictures);
                })
                .toList();

        return new HomeInfoDto(upcomingEvents, 16L, openMemorandos, filesDto, eventDto, monthBirthdaysDto,
                todayBirthdaysDto, feedDto);
    }

    public void clearAllCache() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) cache.clear();
        });
    }
}
