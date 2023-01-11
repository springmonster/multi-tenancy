package com.example.springbootmysqljooq.controller;

import com.khch.jooq.Tables;
import com.khch.jooq.tables.pojos.TOrder;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private DSLContext dslContext;

    @GetMapping("/orders")
    public List<TOrder> getOrders() {
        return dslContext.selectFrom(Tables.T_ORDER)
                .fetchInto(TOrder.class);
    }
}
