package com.beyond.order_system.common.service;

import com.beyond.order_system.common.dto.RabbitMqStockDto;
import com.beyond.order_system.product.domain.Product;
import com.beyond.order_system.product.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RabbitMqStockService {
    private final RabbitTemplate rabbitTemplate;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public RabbitMqStockService(RabbitTemplate rabbitTemplate, ProductRepository productRepository, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }

    public void publish(Long productId, Long productCount) {
        RabbitMqStockDto dto = RabbitMqStockDto.builder()
                .productId(productId)
                .productCount(productCount)
                .build();

        // 채널 1에 발행할지, 2에 발행할지 등을 정해줘야한다. 이를 지정해줘야한다.
        // exchanges, routing key 등을 매개변수로 입력
        rabbitTemplate.convertAndSend("stockQueue", dto);
    }

    // RabbitListener : rabbitMq에 특정 큐에 대해 subscribe하는 어노테이션
    // 사실상 subscribe 프로그램(트랜잭션 분리)을 통해 RDB에 업데이트 쿼리를 발송해주는 것이다.(동시성 이슈 발생 X)
    // 물론 아래의 구조로 개편해도 서버 확장시에 문제가 발생될 수 있다.(갱신이상)
    @Transactional
    @RabbitListener(queues = "stockQueue")
    public void subscribe(Message message) throws JsonProcessingException {
        String messageBody = new String(message.getBody());
        RabbitMqStockDto dto = objectMapper.readValue(messageBody, RabbitMqStockDto.class);
        Product product = productRepository.findById(dto.getProductId()).orElseThrow(() -> new EntityNotFoundException("entity not found"));
        product.decreaseStockQuantity(dto.getProductCount());
    }
}
