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
class TenantSQLExecutingTest {

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

    @Test
    void testOrderTableQuery() {
        MultiTenancyStorage.setTenantID(2);

        Result<Record> fetch = dslContext
                .select(tableByName("PUBLIC", "t_order").fields())
                .from(tableByName("PUBLIC", "t_order"))
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(2, fetch.size());
    }

    @Test
    void testUserAndOrderUnion() {
        MultiTenancyStorage.setTenantID(2);

        Result<Record> fetch = dslContext
                .select(tableByName("PUBLIC", "t_user").fields())
                .from(tableByName("PUBLIC", "t_user"))
                .union(select(tableByName("PUBLIC", "t_order").fields())
                        .from(tableByName("PUBLIC", "t_order")))
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(4, fetch.size());
    }

    @Test
    void testUserAndOrderInnerJoin() {
        MultiTenancyStorage.setTenantID(2);

        Result<Record> fetch = dslContext
                .select(tableByName("PUBLIC", "t_user").fields())
                .from(tableByName("PUBLIC", "t_user"))
                .innerJoin(tableByName("PUBLIC", "t_order"))
                .on(field(name("PUBLIC", "t_user", "user_id"))
                        .eq(field(name("PUBLIC", "t_order", "user_id"), String.class)))
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(2, fetch.size());
    }

    @Test
    void testUserAndOrderNestedQuery() {
        MultiTenancyStorage.setTenantID(2);

        Result<Record> fetch = dslContext
                .select(tableByName("PUBLIC", "t_user").fields())
                .from(tableByName("PUBLIC", "t_user"))
                .where(field(name("PUBLIC", "t_user", "user_id"))
                        .in(select(field(name("PUBLIC", "t_order", "user_id"), String.class))
                                .from(tableByName("PUBLIC", "t_order"))))
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(2, fetch.size());
    }

    @Test
    void testLeftOuterJoinTwoTables() {
        MultiTenancyStorage.setTenantID(4);

        Result<Record> fetch = dslContext.select(tableByName("PUBLIC", "t_user").fields())
                .select(tableByName("PUBLIC", "t_order").fields())
                .from(tableByName("PUBLIC", "t_user"))
                .leftOuterJoin(tableByName("PUBLIC", "t_order"))
                .on(field(name("PUBLIC", "t_user", "user_id"))
                        .eq(field(name("PUBLIC", "t_order", "user_id"), String.class)))
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(2, fetch.size());
    }

    @Test
    void testLeftOuterJoinThreeTables() {
        MultiTenancyStorage.setTenantID(4);

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

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(2, fetch.size());
    }

    @Test
    void testRightOuterJoinTwoTables() {
        MultiTenancyStorage.setTenantID(9);

        Result<Record> fetch = dslContext.select(tableByName("PUBLIC", "t_user").fields())
                .select(tableByName("PUBLIC", "t_user").fields())
                .from(tableByName("PUBLIC", "t_user"))
                .rightOuterJoin(tableByName("PUBLIC", "t_order"))
                .on(field(name("PUBLIC", "t_order", "user_id"))
                        .eq(field(name("PUBLIC", "t_user", "user_id"), String.class)))
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(1, fetch.size());
    }

    @Test
    void testRightOuterJoinThreeTables() {
        MultiTenancyStorage.setTenantID(9);

        Result<Record> fetch = dslContext.select(tableByName("PUBLIC", "t_user").fields())
                .select(tableByName("PUBLIC", "t_order").fields())
                .from(tableByName("PUBLIC", "t_user"))
                .rightOuterJoin(tableByName("PUBLIC", "t_order"))
                .on(field(name("PUBLIC", "t_user", "user_id"))
                        .eq(field(name("PUBLIC", "t_order", "user_id"), String.class)))
                .rightOuterJoin(tableByName("PUBLIC", "t_order_detail"))
                .on(field(name("PUBLIC", "t_order", "order_id"))
                        .eq(field(name("PUBLIC", "t_order_detail", "order_id"), String.class)))
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(6, fetch.size());
    }

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
