package com.integration.test.example;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=DataSourceConfig.class, properties={"datasource.driver=org.h2.Driver",
		"datasource.url=jdbc:h2:mem;MODE=ORACLE",
		"datasource.user=test",
		"datasource.password=test"})
public class ITDatabaseFailureAndRecovery {

	@Test
	public void test() {

	}
}
