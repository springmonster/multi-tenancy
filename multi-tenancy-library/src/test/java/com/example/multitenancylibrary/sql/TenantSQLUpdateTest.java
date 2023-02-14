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
import java.util.UUID;

import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.row;

@SpringBootTest
@ActiveProfiles("test")
class TenantSQLUpdateTest {

    @Autowired
    private DSLContext dslContext;

    private Table userTable;

    @BeforeEach
    public void beforeEach() {
        List<Table<?>> tables = dslContext.meta().getTables(name("PUBLIC", "t_user"));
        userTable = tables.get(0);
    }

    @Test
    void testUserTableUpdate() {
        MultiTenancyStorage.setTenantID(100);

        String updatedValue = UUID.randomUUID().toString();

        int execute = dslContext
                .update(userTable)
                .set(row(userTable.field("user_name")), row(updatedValue))
                .where(userTable.field("user_id").eq("uid100"))
                .execute();
        Assertions.assertEquals(1, execute);

        Result<Record> fetch = dslContext
                .select()
                .from(userTable)
                .fetch();
        Assertions.assertEquals(updatedValue, fetch.getValues("user_name").get(0));
        MultiTenancyStorage.setTenantID(null);
    }
}
