package com.example.multitenancylibrary.sql;

import com.example.multitenancylibrary.network.MultiTenancyStorage;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.jooq.impl.DSL.*;

@SpringBootTest
@ActiveProfiles("test")
class TenantSQLDeleteTest {

    @Autowired
    private DSLContext dslContext;

    @Test
    void testUserTableDelete() {
        MultiTenancyStorage.setTenantID(101);

        int execute = dslContext
                .insertInto(tableByName("PUBLIC", "t_user"),
                        field(name("PUBLIC", "t_user", "user_id")),
                        field(name("PUBLIC", "t_user", "tenant_id")),
                        field(name("PUBLIC", "t_user", "user_name")))
                .values("uid101", 101, "uname101")
                .execute();
        Assertions.assertEquals(1, execute);

        Result<Record> fetch = dslContext
                .select(tableByName("PUBLIC", "t_user").fields())
                .from(tableByName("PUBLIC", "t_user"))
                .fetch();
        Assertions.assertEquals("uname101", fetch.getValues("user_name").get(0));

        int execute1 = dslContext
                .deleteFrom(tableByName("PUBLIC", "t_user"))
                .where(field(name("PUBLIC", "t_user", "user_id")).eq("uid101"))
                .execute();
        Assertions.assertEquals(1, execute1);

        Result<Record> fetch1 = dslContext
                .select(tableByName("PUBLIC", "t_user").fields())
                .from(tableByName("PUBLIC", "t_user"))
                .fetch();
        Assertions.assertEquals(0, fetch1.size());

        MultiTenancyStorage.setTenantID(null);
    }
}
