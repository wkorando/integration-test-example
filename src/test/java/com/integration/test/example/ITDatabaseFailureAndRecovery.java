package com.integration.test.example;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataSourceConfig.class, properties = { "datasource.driver=org.h2.Driver",
		"datasource.url=jdbc:h2:mem:;MODE=ORACLE", "datasource.user=test", "datasource.password=test" })
public class ITDatabaseFailureAndRecovery {

	@Autowired
	private DataSource dataSource;

	@Test
	public void test() throws SQLException {
		Connection conn = DataSourceUtils.getConnection(dataSource);
		conn.createStatement().executeQuery("SELECT 1 FROM dual");
		ResultSet rs = conn.createStatement().executeQuery("SELECT 1 FROM dual");
		assertTrue(rs.next());
		assertEquals(1, rs.getLong(1));
		conn.createStatement().execute("SHUTDOWN");
		DataSourceUtils.releaseConnection(conn, dataSource);
		conn = DataSourceUtils.getConnection(dataSource);
		conn = DataSourceUtils.getConnection(dataSource);
		rs = conn.createStatement().executeQuery("SELECT 1 FROM dual");
		assertTrue(rs.next());
		assertEquals(1, rs.getLong(1));
	}
}
