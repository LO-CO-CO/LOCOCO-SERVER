package com.lokoko.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {

	private static final DockerImageName MYSQL_IMAGE = DockerImageName.parse("mysql:8.0.41");

	@Bean
	@ServiceConnection
	public MySQLContainer mysqlContainer() {
		return new MySQLContainer(MYSQL_IMAGE)
			.withReuse(true);
	}
}
