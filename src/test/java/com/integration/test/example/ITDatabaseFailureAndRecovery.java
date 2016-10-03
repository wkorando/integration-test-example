package com.integration.test.example;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataSourceConfig.class)
public class ITDatabaseFailureAndRecovery {

	private static final Logger LOGGER = LoggerFactory.getLogger(ITDatabaseFailureAndRecovery.class);
    @Autowired
    private DataSource datasource;

    @BeforeClass
    public static void setup() throws IOException, InterruptedException {
        LOGGER.debug("INITIALIZING DB");
        Runtime.getRuntime().exec("/usr/local/mysql/support-files/mysql.server start").waitFor();
        LOGGER.debug("DB INITIALIZED");
    }

    @AfterClass
    public static void cleanup() throws IOException, InterruptedException {
        LOGGER.debug("SHUTTING DOWN DB");
        Runtime.getRuntime().exec("/usr/local/mysql/support-files/mysql.server stop").waitFor();
        LOGGER.debug("DB SHUTDOWN");
    }

    @Test
    public void testServiceRecoveryFromDatabaseOutageAndRecovery() throws SQLException, InterruptedException, IOException {
        Connection conn = null;
        conn = DataSourceUtils.getConnection(datasource);
        assertTrue(conn.createStatement().execute("SELECT 1"));
        DataSourceUtils.releaseConnection(conn, datasource);
        LOGGER.debug("STOPPING DB");
        Runtime.getRuntime().exec("/usr/local/mysql/support-files/mysql.server stop").waitFor();
        LOGGER.debug("DB STOPPED");
        try {
            conn = DataSourceUtils.getConnection(datasource);
            conn.createStatement().execute("SELECT 1");
            fail("Database is down at this point, call should fail");
        } catch (Exception e) {
        	LOGGER.debug("EXPECTED CONNECTION FAILURE");
        }
        LOGGER.debug("STARTING DB");
        Runtime.getRuntime().exec("/usr/local/mysql/support-files/mysql.server start").waitFor();
        LOGGER.debug("DB STARTED");
        conn = DataSourceUtils.getConnection(datasource);
        assertTrue(conn.createStatement().execute("SELECT 1"));
        DataSourceUtils.releaseConnection(conn, datasource);
    }
}
