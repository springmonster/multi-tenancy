package com.example.springbootpostgresjooq.controller;

import com.example.springbootpostgresjooq.model.UserInput;
import com.khch.jooq.Tables;
import com.khch.jooq.tables.pojos.TOrder;
import com.khch.jooq.tables.pojos.TUser;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.khch.jooq.tables.TOrder.T_ORDER;
import static com.khch.jooq.tables.TUser.T_USER;

@RestController
public class UserController {

    @Autowired
    private DSLContext dslContext;

    @GetMapping("/users")
    public List<TUser> getUsers() {
        return dslContext.selectFrom(Tables.T_USER)
                .fetchInto(TUser.class);
    }

    @GetMapping("/users-orders")
    public Map<TUser, List<TOrder>> getUsersOrders() {
        return dslContext.select(T_USER.fields())
                .select(T_ORDER.fields())
                .from(T_USER)
                .rightJoin(T_ORDER)
                .on(T_USER.USER_ID.eq(T_ORDER.USER_ID))
                .fetchGroups(
                        r -> r.into(T_USER).into(TUser.class),
                        r -> r.into(T_ORDER).into(TOrder.class)
                );
    }

    @PostMapping("/user/create")
    public TUser createUser(@RequestBody UserInput userInput) {
        dslContext.insertInto(Tables.T_USER)
                .set(Tables.T_USER.USER_ID, userInput.getUserId())
                .set(Tables.T_USER.TENANT_ID, userInput.getTenantId())
                .set(Tables.T_USER.USER_NAME, userInput.getUserName())
                .execute();

        return dslContext.selectFrom(Tables.T_USER)
                .where(Tables.T_USER.USER_ID.eq(userInput.getUserId()))
                .fetchOneInto(TUser.class);
    }

    @PutMapping("/user/{id}")
    public TUser updateUser(@PathVariable("id") String userId, @RequestParam("username") String userName) {
        dslContext.update(Tables.T_USER)
                .set(Tables.T_USER.USER_NAME, userName)
                .where(Tables.T_USER.USER_ID.eq(userId))
                .execute();

        return dslContext.selectFrom(Tables.T_USER)
                .where(Tables.T_USER.USER_ID.eq(userId))
                .fetchOneInto(TUser.class);
    }

    @DeleteMapping("/user/{id}")
    public TUser deleteUser(@PathVariable("id") String userId) {
        dslContext.deleteFrom(Tables.T_USER)
                .where(Tables.T_USER.USER_ID.eq(userId))
                .execute();

        return dslContext.selectFrom(Tables.T_USER)
                .where(Tables.T_USER.USER_ID.eq(userId))
                .fetchOneInto(TUser.class);
    }
}
