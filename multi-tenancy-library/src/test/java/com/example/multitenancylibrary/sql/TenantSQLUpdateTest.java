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

import java.util.UUID;

import static org.jooq.impl.DSL.*;

@SpringBootTest
@ActiveProfiles("test")
class TenantSQLUpdateTest {

    @Autowired
    private DSLContext dslContext;

    @Test
    void testUserTableUpdate() {
        MultiTenancyStorage.setTenantID(100);

        String updatedValue = UUID.randomUUID().toString();

        int execute = dslContext
                .update(tableByName("PUBLIC", "t_user"))
                .set(row(field(name("PUBLIC", "t_user", "user_name"))), row(updatedValue))
                .where(field(name("PUBLIC", "t_user", "user_id")).eq("uid100"))
                .execute();
        Assertions.assertEquals(1, execute);

        Result<Record> fetch = dslContext
                .select(tableByName("PUBLIC", "t_user").fields())
                .from(tableByName("PUBLIC", "t_user"))
                .fetch();
        Assertions.assertEquals(updatedValue, fetch.getValues("user_name").get(0));
        MultiTenancyStorage.setTenantID(null);
    }
}
