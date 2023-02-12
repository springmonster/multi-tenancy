package com.example.multitenancylibrary.sql;

import com.example.multitenancylibrary.network.MultiTenancyStorage;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
        MultiTenancyStorage.setTenantID(1);
    }

    @AfterAll
    public static void afterAll() {
        MultiTenancyStorage.setTenantID(null);
    }

    @BeforeEach
    void test() {
        int execute = dslContext.
                insertInto(table(name("t_user")))
                .values(1, "uid1", "uname1", 1)
                .execute();
        System.out.println(execute);

        dslContext.
                insertInto(table(name("t_user")))
                .values(2, "uid2", "unam2", 2)
                .execute();

        dslContext.
                insertInto(table(name("t_user")))
                .values(3, "uid3", "uname3", 2)
                .execute();

        dslContext.
                insertInto(table(name("t_user")))
                .values(4, "uid4", "uname4", 3)
                .execute();

        dslContext.
                insertInto(table(name("t_user")))
                .values(5, "uid5", "uname5", 4)
                .execute();

        dslContext.
                insertInto(table(name("t_user")))
                .values(6, "uid6", "uname6", 4)
                .execute();

        Result<Record> fetch = dslContext
                .select(table(name("t_user")).fields())
                .from(tableByName("PUBLIC", "t_user"))
                .fetch();

        System.out.println(fetch);
    }

    @Test
    void testUserTable() {
//        Result<Record> fetch = dslContext
//                .select(table(name("t_user")).fields())
//                .from(tableByName("public", "t_user"))
//                .fetch();
//
//        System.out.println(fetch);
    }
}
