package user.management;

import org.springframework.boot.SpringApplication;

public class TestApplication {

	public static void main(String[] args) {
		SpringApplication.from(UserApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
