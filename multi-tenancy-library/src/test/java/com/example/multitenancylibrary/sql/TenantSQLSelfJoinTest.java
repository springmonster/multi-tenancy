package com.example.multitenancylibrary.sql;

import com.example.multitenancylibrary.network.MultiTenancyStorage;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.jooq.impl.DSL.name;

@SpringBootTest
@ActiveProfiles("test")
public class TenantSQLSelfJoinTest {

    @Autowired
    private DSLContext dslContext;

    private Table departmentTable;

    @BeforeEach
    public void beforeEach() {
        List<Table<?>> tables = dslContext.meta().getTables(name("PUBLIC", "t_department"));
        departmentTable = tables.get(0);
    }

    @Test
    void testSelfLeftJoin() {
        MultiTenancyStorage.setTenantID(1);

        Table c = departmentTable.as("c");
        Result<Record> fetch = dslContext
                .select(c.field("id"),
                        c.field("department_id"),
                        c.field("department_name"),
                        c.field("parent_department_id"),
                        c.field("tenant_id"),
                        departmentTable.field("department_name").as("parent_department_name"))
                .from(c)
                .leftOuterJoin(departmentTable)
                .on(c.field("parent_department_id").eq(departmentTable.field("department_id")))
                .fetch();

        MultiTenancyStorage.setTenantID(null);

        System.out.println(fetch);
    }
}
