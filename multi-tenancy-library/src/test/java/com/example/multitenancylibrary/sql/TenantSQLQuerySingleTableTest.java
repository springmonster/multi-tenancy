package com.example.multitenancylibrary.sql;

import com.example.multitenancylibrary.network.MultiTenancyStorage;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.jooq.impl.DSL.name;

@SpringBootTest
@ActiveProfiles("test")
class TenantSQLQuerySingleTableTest {

    @Autowired
    private DSLContext dslContext;

    private Table<?> userTable;

    @BeforeEach
    public void beforeEach() {
        List<Table<?>> tables = dslContext.meta().getTables(name("PUBLIC", "t_user"));
        userTable = tables.get(0);
    }

    @Test
    void testUserTableQuery() {
        MultiTenancyStorage.setTenantID(4);

        Result<Record> fetch = dslContext
                .select()
                .from(userTable)
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(2, fetch.size());
    }

    @Test
    void testUserTableQueryWithAlias() {
        MultiTenancyStorage.setTenantID(4);

        Table<?> tUserAlias = userTable.as("t_user_alias");

        Result<Record> fetch = dslContext
                .select()
                .from(tUserAlias)
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(2, fetch.size());
    }
}
