package com.toycommerce.user.controller;

import com.toycommerce.user.kafka.MessageProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Kafka Test", description = "카프카 테스트 API")
@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaTestController {

    private final MessageProducer messageProducer;

    @Operation(summary = "메시지 발행", description = "test-topic으로 메시지를 발행합니다.")
    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestParam String message) {
        messageProducer.send(message);
        return ResponseEntity.ok("Message sent: " + message);
    }
}