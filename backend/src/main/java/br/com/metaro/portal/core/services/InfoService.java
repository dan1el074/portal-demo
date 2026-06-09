package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.dto.event.EventDto;
import br.com.metaro.portal.core.dto.info.BirthdayDto;
import br.com.metaro.portal.core.dto.info.HomeInfoDto;
import br.com.metaro.portal.core.repositories.UserRepository;
import br.com.metaro.portal.modules.general.memorando.entities.MemorandoStatus;
import br.com.metaro.portal.modules.general.memorando.repository.MemorandoRepository;
import br.com.metaro.portal.modules.general.post.entities.Post;
import br.com.metaro.portal.modules.general.post.dto.PostDto;
import br.com.metaro.portal.modules.general.post.repositories.PostRepository;
import br.com.metaro.portal.util.smb.files.FileDto;
import br.com.metaro.portal.util.smb.files.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class InfoService {
    @Autowired
    private FileService fileService;
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
        Long openOrders = 16L;
        Long openMemorandos = memorandoRepository.countByStatus(MemorandoStatus.PUBLISH);
        List<FileDto> filesDto = fileService.getFiles();

        EventDto eventDto = eventService.getEvent();

        LocalDate today = LocalDate.now();
        List<BirthdayDto> allBirthdays = userRepository.findBirthdaysOfCurrentMonth()
                .stream()
                .map(BirthdayDto::new)
                .toList();
        List<BirthdayDto> todayBirthdaysDto = allBirthdays.stream()
                .filter(dto -> dto.getDay() == today.getDayOfMonth())
                .toList();
        List<BirthdayDto> monthBirthdaysDto = allBirthdays.stream()
                .filter(dto -> dto.getDay() != today.getDayOfMonth())
                .toList();

        List<Post> feed = postRepository.findTop4ByOrderByIdDesc();
        List<PostDto> feedDto = feed.stream().map(PostDto::new).toList();

        return new HomeInfoDto(upcomingEvents, openOrders, openMemorandos, filesDto, eventDto, monthBirthdaysDto,
                todayBirthdaysDto, feedDto);
    }

    public void clearAllCache() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) cache.clear();
        });
    }
}
