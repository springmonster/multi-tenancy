package com.example.multitenancylibrary.sql;

import com.example.multitenancylibrary.config.MultiTenancyProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

class TenantIDCheckerExecuteListenerTest {

    @Test
    void checkSQlTestForMySQL() {
        // Normal
        String sql = "select `abc_scenario`.`id`, `abc_scenario`.`org_id`, `abc_scenario`.`name`, `abc_scenario`.`name_cn`, `abc_scenario`.`description`, `abc_scenario`.`description_cn`, `abc_scenario`.`installed`, `abc_scenario`.`image`, `abc_scenario`.`parent_scenario_id`, `abc_scenario`.`order`, `abc_scenario`.`created_by`, `abc_scenario`.`created_at`, `abc_scenario`.`updated_by`, `abc_scenario`.`updated_at` from `abc_scenario` where `abc_scenario`.`id` in (4, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 1, 1, 1, 1, 1, 1, 1) and `abc_scenario`.`org_id` in (88)";

        MultiTenancyProperties multiTenancyProperties = new MultiTenancyProperties();
        multiTenancyProperties.setTables(List.of("abc.abc_scenario"));
        multiTenancyProperties.setTenantIdentifier("org_id");

        TenantIDCheckerExecuteListener tenantIDCheckerExecuteListener = new TenantIDCheckerExecuteListener(multiTenancyProperties);

        Assertions.assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(tenantIDCheckerExecuteListener, "checkSQL", sql));

        // Normal
        String sql1 = "select `alias_122582596`.`id`, `alias_122582596`.`org_id`, `alias_122582596`.`name`, `alias_122582596`.`description`, `alias_122582596`.`action_type`, `alias_122582596`.`scenario_id`, `alias_122582596`.`save_path`, `alias_122582596`.`created_by`, `alias_122582596`.`created_at` from (select `abc_action_import`.`id`, `abc_action_import`.`org_id`, `abc_action_import`.`name`, `abc_action_import`.`description`, `abc_action_import`.`action_type`, `abc_action_import`.`scenario_id`, `abc_action_import`.`save_path`, `abc_action_import`.`created_by`, `abc_action_import`.`created_at` from `abc_action_import` where `abc_action_import`.`org_id` in (88)) as `alias_122582596` limit 20";

        multiTenancyProperties.setTables(List.of("abc.abc_action_import"));
        multiTenancyProperties.setTenantIdentifier("org_id");

        Assertions.assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(tenantIDCheckerExecuteListener, "checkSQL", sql1));

        // Exception
        String sql2 = "select count(*) from (select `abc_action_import`.`id`, `abc_action_import`.`org_id`, `abc_action_import`.`name`, `abc_action_import`.`description`, `abc_action_import`.`action_type`, `abc_action_import`.`scenario_id`, `abc_action_import`.`save_path`, `abc_action_import`.`created_by`, `abc_action_import`.`created_at` from `abc_action_import`) as `alias_122582596`";

        multiTenancyProperties.setTables(List.of("abc.abc_action_import"));
        multiTenancyProperties.setTenantIdentifier("org_id");

        Assertions.assertThrows(TenantIDException.class, () -> ReflectionTestUtils.invokeMethod(tenantIDCheckerExecuteListener, "checkSQL", sql2));
    }

    @Test
    void checkSQLTestForPostgreSQL() {
        String sql = "select \"public\".\"t_user\".\"id\", \"public\".\"t_user\".\"user_id\", \"public\".\"t_user\".\"tenant_id\", \"public\".\"t_user\".\"user_name\" from \"public\".\"t_user\" where \"public\".\"t_user\".\"tenant_id\" in (?)";

        MultiTenancyProperties multiTenancyProperties = new MultiTenancyProperties();
        multiTenancyProperties.setTables(List.of("public.t_user"));
        multiTenancyProperties.setTenantIdentifier("tenant_id");

        TenantIDCheckerExecuteListener tenantIDCheckerExecuteListener = new TenantIDCheckerExecuteListener(multiTenancyProperties);

        Assertions.assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(tenantIDCheckerExecuteListener, "checkSQL", sql));
    }

    @Test
    void checkSQLRegex() {
        String sql1 = "select * from t_user where t_user.tenant_id in 1";
        String sql2 = "select * from t_user where t_user.tenant_id = 1";
        String sql3 = "select * from t_user where 1=1 and t_user.tenant_id in 1";
        String sql4 = "select * from t_user where 1=1 and t_user.tenant_id = 1";
        String sql5 = "select * from t_user where 1=1";
        String sql6 = "select * from t_user";
        String sql7 = "update t_user set tenant_id = 1 where 1=1";
        String sql8 = "delete from t_user where 1=1";

        MultiTenancyProperties multiTenancyProperties = new MultiTenancyProperties();
        multiTenancyProperties.setTables(List.of("public.t_user"));
        multiTenancyProperties.setTenantIdentifier("tenant_id");

        TenantIDCheckerExecuteListener tenantIDCheckerExecuteListener = new TenantIDCheckerExecuteListener(multiTenancyProperties);

        Boolean b1 = ReflectionTestUtils.invokeMethod(tenantIDCheckerExecuteListener, "isConditionExistInSQLRegex", sql1);
        Boolean b2 = ReflectionTestUtils.invokeMethod(tenantIDCheckerExecuteListener, "isConditionExistInSQLRegex", sql2);
        Boolean b3 = ReflectionTestUtils.invokeMethod(tenantIDCheckerExecuteListener, "isConditionExistInSQLRegex", sql3);
        Boolean b4 = ReflectionTestUtils.invokeMethod(tenantIDCheckerExecuteListener, "isConditionExistInSQLRegex", sql4);
        Boolean b5 = ReflectionTestUtils.invokeMethod(tenantIDCheckerExecuteListener, "isConditionExistInSQLRegex", sql5);
        Boolean b6 = ReflectionTestUtils.invokeMethod(tenantIDCheckerExecuteListener, "isConditionExistInSQLRegex", sql6);
        Boolean b7 = ReflectionTestUtils.invokeMethod(tenantIDCheckerExecuteListener, "isConditionExistInSQLRegex", sql7);
        Boolean b8 = ReflectionTestUtils.invokeMethod(tenantIDCheckerExecuteListener, "isConditionExistInSQLRegex", sql8);
        Assertions.assertEquals(Boolean.TRUE, b1);
        Assertions.assertEquals(Boolean.TRUE, b2);
        Assertions.assertEquals(Boolean.TRUE, b3);
        Assertions.assertEquals(Boolean.TRUE, b4);
        Assertions.assertEquals(Boolean.FALSE, b5);
        Assertions.assertEquals(Boolean.FALSE, b6);
        Assertions.assertEquals(Boolean.FALSE, b7);
        Assertions.assertEquals(Boolean.FALSE, b8);
    }
}