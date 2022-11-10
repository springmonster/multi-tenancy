package com.example.springbootpostgresjooq.controller;

import com.khch.jooq.Tables;
import com.khch.jooq.tables.pojos.TOrder;
import com.khch.jooq.tables.pojos.TUser;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
                .join(T_ORDER)
                .on(T_USER.USER_ID.eq(T_ORDER.USER_ID))
                .fetchGroups(
                        r -> r.into(T_USER).into(TUser.class),
                        r -> r.into(T_ORDER).into(TOrder.class)
                );
    }
}
