package com.example.springbootpostgresjooq.tenant;

import org.jooq.ConnectionProvider;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class RlsConnectionProvider implements ConnectionProvider {

    @Autowired
    DataSource ds;

    @Override
    public Connection acquire() {
        try {
            Connection connection = ds.getConnection();
            try (Statement sql = connection.createStatement()) {
                sql.execute("SET app.current_tenant = '" + ThreadLocalStorage.getTenantID() + "'");
            }
            return connection;
        } catch (SQLException e) {
            throw new DataAccessException("Something failed", e);
        }
    }

    @Override
    public void release(Connection c) {
        try {
            c.close();
        } catch (SQLException e) {
            throw new DataAccessException("Something failed", e);
        }
    }
}