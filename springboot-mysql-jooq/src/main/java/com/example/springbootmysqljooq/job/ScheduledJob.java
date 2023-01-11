package com.example.springbootmysqljooq.job;

import com.example.multitenancylibrary.network.MultiTenancyStorage;
import com.khch.jooq.Tables;
import com.khch.jooq.tables.pojos.TUser;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledJob {

    @Autowired
    private DSLContext dslContext;

    //    @Scheduled(cron = "0/10 0/1 * * * ? ")
    public void schedule() {
        MultiTenancyStorage.setTenantID(1);

        List<TUser> tUsers = dslContext.selectFrom(Tables.T_USER)
                .fetchInto(TUser.class);

        System.out.println(tUsers);

        MultiTenancyStorage.setTenantID(null);
    }
}
