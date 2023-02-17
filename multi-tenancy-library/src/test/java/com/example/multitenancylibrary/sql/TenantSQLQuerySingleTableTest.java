package com.example.multitenancylibrary.sql;

import com.example.multitenancylibrary.network.MultiTenancyStorage;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
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
class TenantSQLQuerySingleTableTest {

    @Autowired
    private DSLContext dslContext;

    private Table<?> userTable;

    @BeforeEach
    public void beforeEach() {
        List<Table<?>> tables = dslContext.meta().getTables("t_user");
        userTable = tables.get(0);
    }

    @Test
    void testUserTableQuery() {
        MultiTenancyStorage.setTenantID(4);

        Result<Record> fetch = dslContext
                .select()
                .from(userTable)
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(2, fetch.size());
    }

    @Test
    void testUserTableQueryWithWhere() {
        MultiTenancyStorage.setTenantID(4);

        Result<Record> fetch = dslContext
                .select()
                .from(userTable)
                .where(userTable.field("tenant_id", Integer.class).eq(4))
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(2, fetch.size());
    }

    @Test
    void testUserTableQueryWithAlias() {
        MultiTenancyStorage.setTenantID(4);

        Table<?> tUserAlias = userTable.as("t_user_alias");

        Result<Record> fetch = dslContext
                .select()
                .from(tUserAlias)
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(2, fetch.size());
    }

    @Test
    void testUserTableQueryDistinct() {
        MultiTenancyStorage.setTenantID(1);

        Result<? extends Record1<?>> userId = dslContext
                .selectDistinct(userTable.field("user_id"))
                .from(userTable)
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(2, userId.size());
    }

    @Test
    void testUserTableQueryCount() {
        MultiTenancyStorage.setTenantID(1);

        int count = dslContext
                .selectCount()
                .from(userTable)
                .fetchOne(0, int.class);

        MultiTenancyStorage.setTenantID(null);

        Assertions.assertEquals(3, count);
    }

    @Test
    void testUserTablePlainQuery() {
        Assertions.assertThrows(TenantIDException.class, () -> {
            MultiTenancyStorage.setTenantID(1);

            dslContext.fetch("select * from t_user");

            MultiTenancyStorage.setTenantID(null);
        });
    }

    @Test
    void testUserTablePlainQueryWithSchema() {
        Assertions.assertThrows(TenantIDException.class, () -> {
            MultiTenancyStorage.setTenantID(1);

            dslContext.fetch("select * from public.t_user");

            MultiTenancyStorage.setTenantID(null);
        });
    }
}
