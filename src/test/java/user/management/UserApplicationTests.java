package user.management;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import user.management.dto.UserDto;
import user.management.dto.http.UserCreateRequest;
import user.management.dto.http.UserResponse;
import user.management.service.UserService;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

//@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath*:application.properties")
@EmbeddedKafka(partitions = 1, topics = "test.users.topic",
		brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class UserApplicationTests extends AbstractIntegrationTest {

	@Autowired
	UserService userService;

	@Test
	@Transactional
	void test_user_create() {
		UserCreateRequest request = new UserCreateRequest();
		request.setUsername("user-1234");
		request.setEmail("user-1234@gmail.com");
		request.setPassword("password-12345678");
		request.setRePassword("password-12345678");

		// Call the method to test
		UserResponse response = userService.createUser(request);

		// Verify results
        assertEquals("user created", response.getMessage());

		UserDto userDto = userService.getUser(response.getUserId());

		assertThat(userDto).isNotNull();
		assertThat(userDto.getUsername()).isEqualTo(request.getUsername());

		kafkaTemplate.send("test.users.topic", userDto.getUsername(), userDto.toString());

		// Verify event sending
		Consumer<String, String> consumer = configureConsumer();
		ConsumerRecord<String, String> record =
				KafkaTestUtils.getSingleRecord(consumer, "test.users.topic");
		assertEquals(userDto.getUsername(), record.key());//key is username
	}

	@Autowired
	EmbeddedKafkaBroker embeddedKafkaBroker;

	@Autowired
	KafkaTemplate<String, Object> kafkaTemplate;

	private Consumer<String, String> configureConsumer() {
		Map<String, Object> consumerProps =
				KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
		consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		Consumer<String, String> consumer =
				new DefaultKafkaConsumerFactory<String, String>(consumerProps).createConsumer();
		consumer.subscribe(Collections.singleton("test.users.topic"));
		return consumer;
	}
}
