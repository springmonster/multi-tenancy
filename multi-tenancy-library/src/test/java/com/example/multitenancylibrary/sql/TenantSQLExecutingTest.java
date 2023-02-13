package com.example.multitenancylibrary.sql;

import com.example.multitenancylibrary.network.MultiTenancyStorage;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.jooq.impl.DSL.*;

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
                .select(tableByName("PUBLIC", "t_user").fields())
                .from(tableByName("PUBLIC", "t_user"))
                .fetch();

        System.out.println(fetch);
        Assertions.assertEquals(2, fetch.size());
    }

    @Test
    void testLeftOuterJoinTwoTables() {
        Result<Record> fetch = dslContext.select(tableByName("PUBLIC", "t_user").fields())
                .select(tableByName("PUBLIC", "t_order").fields())
                .from(tableByName("PUBLIC", "t_user"))
                .leftOuterJoin(tableByName("PUBLIC", "t_order"))
                .on(field(name("PUBLIC", "t_user", "user_id"))
                        .eq(field(name("PUBLIC", "t_order", "user_id"), String.class)))
                .fetch();

        System.out.println(fetch);
        Assertions.assertEquals(2, fetch.size());
    }

    // TODO: 2023/2/13 failed, checking 
    @Test
    void testLeftOuterJoinThreeTables() {
        Result<Record> fetch = dslContext.select(tableByName("PUBLIC", "t_user").fields())
                .select(tableByName("PUBLIC", "t_order").fields())
                .from(tableByName("PUBLIC", "t_user"))
                .leftOuterJoin(tableByName("PUBLIC", "t_order"))
                .on(field(name("PUBLIC", "t_user", "user_id"))
                        .eq(field(name("PUBLIC", "t_order", "user_id"), String.class)))
                .leftOuterJoin(tableByName("PUBLIC", "t_order_detail"))
                .on(field(name("PUBLIC", "t_order", "order_id"))
                        .eq(field(name("PUBLIC", "t_order_detail", "order_id"), String.class)))
                .fetch();

        System.out.println(fetch);
        Assertions.assertEquals(2, fetch.size());
    }
}
