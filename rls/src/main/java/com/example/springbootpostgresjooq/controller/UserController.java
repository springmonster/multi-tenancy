package com.example.springbootpostgresjooq.controller;

import com.example.springbootpostgresjooq.model.UserInput;
import com.khch.jooq.Tables;
import com.khch.jooq.tables.pojos.TUser;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class UserController {

    @Autowired
    private DSLContext dslContext;

    @GetMapping("/users")
    public List<TUser> getUsers() {
        return dslContext.selectFrom(Tables.T_USER)
                .fetchInto(TUser.class);
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
