-- t_user
INSERT INTO t_user ("user_id", "tenant_id", "user_name")
VALUES ('uid0', 1, 'uname0');
INSERT INTO t_user ("user_id", "tenant_id", "user_name")
VALUES ('uid1', 1, 'uname1');
INSERT INTO t_user ("user_id", "tenant_id", "user_name")
VALUES ('uid1', 1, 'uname1');
INSERT INTO t_user ("user_id", "tenant_id", "user_name")
VALUES ('uid2', 2, 'uname2');
INSERT INTO t_user ("user_id", "tenant_id", "user_name")
VALUES ('uid3', 2, 'uname3');
INSERT INTO t_user ("user_id", "tenant_id", "user_name")
VALUES ('uid4', 4, 'uname4');
INSERT INTO t_user ("user_id", "tenant_id", "user_name")
VALUES ('uid5', 4, 'uname5');
INSERT INTO t_user ("user_id", "tenant_id", "user_name")
VALUES ('uid6', 5, 'uname6');
INSERT INTO t_user ("user_id", "tenant_id", "user_name")
VALUES ('uid9', 9, 'uname9');
INSERT INTO t_user ("user_id", "tenant_id", "user_name")
VALUES ('uid100', 100, 'uname100');

-- t_order
INSERT INTO t_order ("user_id", "order_id", "tenant_id")
VALUES ('uid1', 'ord1', 1);
INSERT INTO t_order ("user_id", "order_id", "tenant_id")
VALUES ('uid2', 'ord1', 2);
INSERT INTO t_order ("user_id", "order_id", "tenant_id")
VALUES ('uid3', 'ord1', 2);
INSERT INTO t_order ("user_id", "order_id", "tenant_id")
VALUES ('uid9', 'ord1', 9);
INSERT INTO t_order ("user_id", "order_id", "tenant_id")
VALUES ('uid9', 'ord2', 9);
INSERT INTO t_order ("user_id", "order_id", "tenant_id")
VALUES ('uid10', 'ord1', 9);

-- t_order_detail
INSERT INTO t_order_detail ("order_id", "quantity", "tenant_id")
VALUES ('ord1', 1, 1);
INSERT INTO t_order_detail ("order_id", "quantity", "tenant_id")
VALUES ('ord2', 2, 2);
INSERT INTO t_order_detail ("order_id", "quantity", "tenant_id")
VALUES ('ord3', 3, 3);
INSERT INTO t_order_detail ("order_id", "quantity", "tenant_id")
VALUES ('ord4', 4, 4);
INSERT INTO t_order_detail ("order_id", "quantity", "tenant_id")
VALUES ('ord9', 4, 9);
INSERT INTO t_order_detail ("order_id", "quantity", "tenant_id")
VALUES ('ord1', 4, 9);
INSERT INTO t_order_detail ("order_id", "quantity", "tenant_id")
VALUES ('ord1', 4, 9);
INSERT INTO t_order_detail ("order_id", "quantity", "tenant_id")
VALUES ('ord1', 4, 9);

-- t_department
INSERT INTO t_department ("department_id", "department_name", "tenant_id")
VALUES (1, 'aaa', 1);
INSERT INTO t_department ("department_id", "department_name", "parent_department_id", "tenant_id")
VALUES (2, 'bbb', 1, 1);
INSERT INTO t_department ("department_id", "department_name", "parent_department_id", "tenant_id")
VALUES (3, 'ccc', 1, 1);
INSERT INTO t_department ("department_id", "department_name", "parent_department_id", "tenant_id")
VALUES (4, 'ddd', 1, 1);
INSERT INTO t_department ("department_id", "department_name", "parent_department_id", "tenant_id")
VALUES (5, 'bbb', 2, 1);
INSERT INTO t_department ("department_id", "department_name", "parent_department_id", "tenant_id")
VALUES (6, 'ccc', 2, 1);
INSERT INTO t_department ("department_id", "department_name", "parent_department_id", "tenant_id")
VALUES (7, 'ddd', 2, 1);