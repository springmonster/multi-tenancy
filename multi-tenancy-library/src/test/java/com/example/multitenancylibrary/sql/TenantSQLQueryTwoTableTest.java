package com.example.multitenancylibrary.sql;

import com.example.multitenancylibrary.network.MultiTenancyStorage;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.jooq.impl.DSL.select;

@SpringBootTest
@ActiveProfiles("test")
class TenantSQLQueryTwoTableTest {

    @Autowired
    private DSLContext dslContext;

    private Table userTable;

    private Table orderTable;

    @BeforeEach
    public void beforeEach() {
        List<Table<?>> tables = dslContext.meta().getTables("t_user");
        userTable = tables.get(0);

        List<Table<?>> tables1 = dslContext.meta().getTables("t_order");
        orderTable = tables1.get(0);
    }

    @Test
    void testUserAndOrderUnion() {
        MultiTenancyStorage.setTenantID(2);

        Result<Record> fetch = dslContext
                .select()
                .from(userTable)
                .union(select()
                        .from(orderTable))
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(4, fetch.size());
    }

    @Test
    void testUserAndOrderInnerJoin() {
        MultiTenancyStorage.setTenantID(2);

        Result<Record> fetch = dslContext
                .select()
                .from(userTable)
                .innerJoin(orderTable)
                .on(userTable.field("user_id")
                        .eq(orderTable.field("user_id")))
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(2, fetch.size());
    }

    @Test
    void testUserAndOrderNestedQuery() {
        MultiTenancyStorage.setTenantID(2);

        Result<Record> fetch = dslContext
                .select()
                .from(userTable)
                .where(userTable.field("user_id")
                        .in(select(orderTable.field("user_id"))
                                .from(orderTable)))
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(2, fetch.size());
    }


    @Nested
    @DisplayName("Left Outer Join || Right Outer Join")
    class LeftOrRightOuterJoinTest {
        @Test
        void testLeftOuterJoinTwoTables() {
            MultiTenancyStorage.setTenantID(4);

            Result<Record> fetch = dslContext
                    .select(userTable.fields())
                    .select(orderTable.fields())
                    .from(userTable)
                    .leftOuterJoin(orderTable)
                    .on(userTable.field("user_id")
                            .eq(orderTable.field("user_id")))
                    .fetch();

            MultiTenancyStorage.setTenantID(null);

            Assertions.assertEquals(2, fetch.size());
        }

        @Test
        void testRightOuterJoinTwoTables() {
            MultiTenancyStorage.setTenantID(9);

            Result<Record> fetch = dslContext
                    .select(userTable.fields())
                    .select(orderTable.fields())
                    .from(userTable)
                    .rightOuterJoin(orderTable)
                    .on(orderTable.field("user_id")
                            .eq(userTable.field("user_id")))
                    .fetch();

            MultiTenancyStorage.setTenantID(null);

            Assertions.assertEquals(3, fetch.size());
        }
    }
}
