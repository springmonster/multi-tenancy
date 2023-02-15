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
class TenantSQLQueryThreeTableTest {

    @Autowired
    private DSLContext dslContext;

    private Table userTable;

    private Table orderTable;

    private Table orderDetailTable;

    @BeforeEach
    public void beforeEach() {
        List<Table<?>> tables = dslContext.meta().getTables("t_user");
        userTable = tables.get(0);

        List<Table<?>> tables1 = dslContext.meta().getTables("t_order");
        orderTable = tables1.get(0);

        List<Table<?>> tables2 = dslContext.meta().getTables("t_order_detail");
        orderDetailTable = tables2.get(0);
    }

    @Test
    void testLeftOuterJoinThreeTables() {
        MultiTenancyStorage.setTenantID(4);

        Result<Record> fetch = dslContext
                .select(userTable.fields())
                .select(orderTable.fields())
                .select(orderDetailTable.fields())
                .from(userTable)
                .leftOuterJoin(orderTable)
                .on(userTable.field("user_id")
                        .eq(orderTable.field("user_id")))
                .leftOuterJoin(orderDetailTable)
                .on(orderTable.field("order_id")
                        .eq(orderDetailTable.field("order_id")))
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(2, fetch.size());
    }

    @Test
    void testRightOuterJoinThreeTables() {
        MultiTenancyStorage.setTenantID(9);

        Result<Record> fetch = dslContext
                .select(userTable.fields())
                .select(orderTable.fields())
                .select(orderDetailTable.fields())
                .from(userTable)
                .rightOuterJoin(orderTable)
                .on(userTable.field("user_id")
                        .eq(orderTable.field("user_id")))
                .rightOuterJoin(orderDetailTable)
                .on(orderTable.field("order_id")
                        .eq(orderDetailTable.field("order_id")))
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(6, fetch.size());
    }
}
