-- t_user
INSERT INTO PUBLIC.t_user ("user_id", "tenant_id", "user_name")
VALUES ('uid1', 1, 'uname1');
INSERT INTO PUBLIC.t_user ("user_id", "tenant_id", "user_name")
VALUES ('uid2', 2, 'uname2');
INSERT INTO PUBLIC.t_user ("user_id", "tenant_id", "user_name")
VALUES ('uid3', 2, 'uname3');
INSERT INTO PUBLIC.t_user ("user_id", "tenant_id", "user_name")
VALUES ('uid4', 4, 'uname4');
INSERT INTO PUBLIC.t_user ("user_id", "tenant_id", "user_name")
VALUES ('uid5', 4, 'uname5');
INSERT INTO PUBLIC.t_user ("user_id", "tenant_id", "user_name")
VALUES ('uid6', 5, 'uname6');

-- t_order
INSERT INTO PUBLIC.t_order ("user_id", "order_id", "tenant_id")
VALUES ('uid1', 'ord1', 1);
INSERT INTO PUBLIC.t_order ("user_id", "order_id", "tenant_id")
VALUES ('uid2', 'ord1', 2);
INSERT INTO PUBLIC.t_order ("user_id", "order_id", "tenant_id")
VALUES ('uid3', 'ord1', 2);

-- t_order_detail order_id, quantity,
INSERT INTO PUBLIC.t_order_detail ("order_id", "quantity", "tenant_id")
VALUES ('ord1', 1, 1);
INSERT INTO PUBLIC.t_order_detail ("order_id", "quantity", "tenant_id")
VALUES ('ord2', 2, 2);
INSERT INTO PUBLIC.t_order_detail ("order_id", "quantity", "tenant_id")
VALUES ('ord3', 3, 3);
INSERT INTO PUBLIC.t_order_detail ("order_id", "quantity", "tenant_id")
VALUES ('ord4', 4, 4);