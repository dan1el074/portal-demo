package br.com.metaro.portal.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();

        manager.registerCustomCache(
                "homeInfo",
                Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(100).build()
        );

        manager.registerCustomCache(
                "files",
                Caffeine.newBuilder().expireAfterWrite(24, TimeUnit.HOURS).maximumSize(100).build()
        );

        manager.registerCustomCache(
                "events",
                Caffeine.newBuilder().expireAfterWrite(4, TimeUnit.HOURS).maximumSize(100).build()
        );

        manager.registerCustomCache(
                "eventCount",
                Caffeine.newBuilder().expireAfterWrite(4, TimeUnit.HOURS).maximumSize(100).build()
        );

        return manager;
    }
}
