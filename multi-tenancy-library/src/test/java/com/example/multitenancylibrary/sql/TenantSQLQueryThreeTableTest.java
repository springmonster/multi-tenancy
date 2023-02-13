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
class TenantSQLQueryThreeTableTest {

    @Autowired
    private DSLContext dslContext;
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
}
