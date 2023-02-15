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

@SpringBootTest
@ActiveProfiles("test")
class TenantSQLDeleteTest {

    @Autowired
    private DSLContext dslContext;

    private Table userTable;

    @BeforeEach
    public void beforeEach() {
        List<Table<?>> tables = dslContext.meta().getTables("t_user");
        userTable = tables.get(0);
    }

    @Test
    void testUserTableDelete() {
        MultiTenancyStorage.setTenantID(101);

        int execute = dslContext
                .insertInto(userTable,
                        userTable.field("user_id"),
                        userTable.field("tenant_id"),
                        userTable.field("user_name"))
                .values("uid101", 101, "uname101")
                .execute();
        Assertions.assertEquals(1, execute);

        Result<Record> fetch = dslContext
                .select()
                .from(userTable)
                .fetch();
        Assertions.assertEquals("uname101", fetch.getValues("user_name").get(0));

        int execute1 = dslContext
                .deleteFrom(userTable)
                .where(userTable.field("user_id").eq("uid101"))
                .execute();
        Assertions.assertEquals(1, execute1);

        Result<Record> fetch1 = dslContext
                .select()
                .from(userTable)
                .fetch();
        Assertions.assertEquals(0, fetch1.size());

        MultiTenancyStorage.setTenantID(null);
    }
}
