package com.example.multitenancylibrary.sql;

import com.example.multitenancylibrary.network.MultiTenancyStorage;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.jooq.impl.DSL.tableByName;

@SpringBootTest
@ActiveProfiles("test")
class TenantSQLQuerySingleTableTest {

    @Autowired
    private DSLContext dslContext;

    @Test
    void testUserTableQuery() {
        MultiTenancyStorage.setTenantID(4);

        Result<Record> fetch = dslContext
                .select(tableByName("PUBLIC", "t_user").fields())
                .from(tableByName("PUBLIC", "t_user"))
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(2, fetch.size());
    }

    // TODO: 2023/2/13 why is can't execute? 
    @Test
    void testUserTableQueryWithAlias() {
        MultiTenancyStorage.setTenantID(4);

        Table<Record> table = tableByName("PUBLIC", "t_user").as(tableByName("PUBLIC", "t_user"));

        Result<Record> fetch = dslContext
                .select(table.fields())
                .from(table)
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(2, fetch.size());
    }
}
