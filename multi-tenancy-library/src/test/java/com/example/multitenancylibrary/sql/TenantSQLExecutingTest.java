package com.example.multitenancylibrary.sql;

import com.example.multitenancylibrary.network.MultiTenancyStorage;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.tableByName;

@SpringBootTest
@ActiveProfiles("test")
class TenantSQLExecutingTest {

    @Autowired
    private DSLContext dslContext;

    @BeforeAll
    public static void beforeAll() {
        MultiTenancyStorage.setTenantID(4);
    }

    @AfterAll
    public static void afterAll() {
        MultiTenancyStorage.setTenantID(null);
    }

    @Test
    void testUserTable() {
        Result<Record> fetch = dslContext
                .select(table(name("t_user")).fields())
                .from(tableByName("PUBLIC", "t_user"))
                .fetch();

        System.out.println(fetch);
    }

    @Test
    void testLeftOuterJoin() {
        Field<?> field = table(name("t_order")).field(name("user_id"));

        Result<Record> fetch = dslContext.select(table(name("t_user")).fields())
                .select(table(name("t_order")).fields())
                .from(tableByName("PUBLIC", "t_user"))
                .leftOuterJoin(table(name("t_order")))
                .on(field(name("t_user", "user_id"))
                        .eq(field(name("t_order", "user_id"), String.class)))
                .fetch();

        System.out.println(fetch);
    }
}
