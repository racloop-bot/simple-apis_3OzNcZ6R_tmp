package com.jio.fabric.demo.lowercase.service;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ril.fabric.config.Event;
import com.ril.fabric.demo.LowerCaseText;
import com.ril.fabric.demo.PlainText;
import com.ril.fabric.executor.serdes.EventDeserializer;
import com.ril.fabric.executor.serdes.EventSerializer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"e-PlainText-28ebd9a", "e-LowerCaseText-28ebd9a"})
@TestPropertySource(properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
class ToLowerCaseIT {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Producer<String, Event> producer;
    private Consumer<String, Event> consumer;
    private BlockingQueue<ConsumerRecord<String, Event>> records;
    private KafkaMessageListenerContainer<String, Event> container;

    @BeforeEach
    void setUp() {
        Map<String, Object> producerConfig = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producer = new DefaultKafkaProducerFactory<>(producerConfig, new StringSerializer(), new EventSerializer()).createProducer();

        Map<String, Object> consumerConfig = KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker);
        consumer = new DefaultKafkaConsumerFactory<>(consumerConfig, new StringDeserializer(), new EventDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @Timeout(5)
    public void testChangeInCase() throws InvalidProtocolBufferException, InterruptedException {
        PlainText plainText = PlainText.newBuilder().setText("Success is How High You Bounce When You Hit Bottom.").build();
        Any payload = Any.pack(plainText);
        Event eventIn = Event.newBuilder().setEventId("event-1").setEventTopic("e-PlainText-28ebd9a").setEventTopic(plainText.getClass().getSimpleName()).setPayload(payload).build();
        System.out.println(eventIn);
        ProducerRecord<String, Event> producerRecord = new ProducerRecord<>("e-PlainText-28ebd9a", eventIn);
        producer.send(producerRecord);
        producer.flush();

        ConsumerRecord<String, Event> toLowerCase = KafkaTestUtils.getSingleRecord(consumer, "e-LowerCaseText-28ebd9a");
        Event event = toLowerCase.value();
        System.out.println(event);
        payload = event.getPayload();
        LowerCaseText lowerCaseText = payload.unpack(LowerCaseText.class);
        Assertions.assertEquals("success is how high you bounce when you hit bottom.", lowerCaseText.getText());
    }

}