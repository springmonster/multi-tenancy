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
class TenantSQLQueryTwoTableTest {

    @Autowired
    private DSLContext dslContext;

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

        Assertions.assertEquals(3, fetch.size());
    }
}
