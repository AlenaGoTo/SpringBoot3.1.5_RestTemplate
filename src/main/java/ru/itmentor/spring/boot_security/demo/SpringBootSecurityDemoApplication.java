package ru.itmentor.spring.boot_security.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.itmentor.spring.boot_security.demo.model.User;

import java.util.List;

@SpringBootApplication
public class SpringBootSecurityDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityDemoApplication.class, args);

		// RestTemplate - клиент для выполнения HTTP-запросов
		// считается устаревшим. Для асинхронных и потоковых сценариев рассмотрите реактивный WebClient.
		RestTemplate restTemplate = new RestTemplate();

		// GET запрос получения списка юзеров
		ResponseEntity<List<User>> response = restTemplate.exchange(
				"http://94.198.50.185:7081/api/users",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<User>>() {}
		);
		List<User> jsonObjects = response.getBody();
		System.out.println(jsonObjects.get(0));
		System.out.println(jsonObjects.get(1));

		// кука из запроса
		// отправка идентификатора сессии JSESSIONID явным образом через куки сообщает серверу, что все запросы выполняются в рамках одной сесии
		// по JSESSIONID сервер может восстановить сессию из своего хранилища
		// для @RestController единство сессии обеспечивает @Autowired public HttpSession httpSession;
		String cookie = response.getHeaders().get("Set-Cookie").get(0);
		System.out.println(response.getHeaders());
		System.out.println(cookie);

		// POST
		User addedUser = new User(3L,"James","Brown", (byte) 25);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", cookie);
		// headers.add("Content-Type", "application/json"); //headers.setContentType(MediaType.APPLICATION_JSON);
		System.out.println(headers);
		HttpEntity<User> entity = new HttpEntity<>(addedUser, headers); // body и headers
		ResponseEntity<String> response1 = restTemplate.postForEntity("http://94.198.50.185:7081/api/users", entity, String.class);
		String result1 = response1.getBody(); // 1 часть ключа

		// PUT
		ResponseEntity<String> response2 = restTemplate.exchange("http://94.198.50.185:7081/api/users",
				HttpMethod.PUT, entity, String.class);
		String result2 = response2.getBody(); // 2 часть ключа

		// DELETE
		HttpEntity<User> entity2 = new HttpEntity<>(headers);
		ResponseEntity<String> response3 = restTemplate.exchange("http://94.198.50.185:7081/api/users/3",
				HttpMethod.DELETE, entity2, String.class);
		String result3 = response3.getBody(); // 3 часть ключа

		// Код полностью
		System.out.printf("Code: %s%s%s", result1, result2, result3);

	}

}
