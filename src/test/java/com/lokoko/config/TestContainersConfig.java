package com.lokoko.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfig {

	private static final String MYSQL_IMAGE = "mysql:8.0.41";

	@Bean
	@ServiceConnection
	public MySQLContainer mysqlContainer() {
		return new MySQLContainer(DockerImageName.parse(MYSQL_IMAGE))
			.withReuse(true);
	}
}
