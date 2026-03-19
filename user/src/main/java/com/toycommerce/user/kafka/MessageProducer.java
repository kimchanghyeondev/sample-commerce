package com.toycommerce.user.kafka;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class MessageProducer {
//
//    private final KafkaTemplate<String, String> kafkaTemplate;
//
//    private static final String TOPIC = "test-topic";
//
//    public void send(String message) {
//        log.info("Sending message: {}", message);
//        kafkaTemplate.send(TOPIC, message);
//    }
//}
