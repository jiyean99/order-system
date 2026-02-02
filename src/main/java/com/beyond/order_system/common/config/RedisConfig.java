package com.beyond.order_system.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;
    /*
     * [연결 빈객체]
     * - redis에 대한 연결 정보(Host, Port, DB 번호)
     *
     * [템플릿 빈객체]
     * - 자료구조 설계
     *
     * [@Qualifier]
     * - 같은 Bean 객체가 여러개 있을 경우, Bean객체를 구분하기 위한 어노테이션
     * */

    // 연결 빈객체
    @Bean
    @Qualifier("rtInventory")
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setDatabase(0);
        return new LettuceConnectionFactory();
    }

    // 템플릿 빈객체
    @Bean
    @Qualifier("rtInventory")
    // 모든 template 중에 무조건 redisTemplate이라는 메서드명이 반드시 한 개는 있어야함.
    public RedisTemplate<String, String> redisTemplate(@Qualifier("rtInventory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        // key와 value를 String으로 만들어서 저장하겠다는 설정(내부적으로 자료구조에 대한 태깅은 갖고있다)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        // 우리가 생성한 연결 빈 객체를 넘겨주는 작업
        // 매개변수로 주입받는 독특한 형태
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    // 연결 빈객체
    @Bean
    @Qualifier("stockInventory")
    public RedisConnectionFactory redisStockConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setDatabase(1);
        return new LettuceConnectionFactory();
    }

    // 템플릿 빈객체
    @Bean
    @Qualifier("stockInventory")
    public RedisTemplate<String, String> redisStockTemplate(@Qualifier("stockInventory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}
