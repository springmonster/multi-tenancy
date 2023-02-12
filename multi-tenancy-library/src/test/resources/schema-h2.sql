CREATE SCHEMA IF NOT EXISTS PUBLIC;

DROP TABLE IF EXISTS t_user;
DROP TABLE IF EXISTS t_order;

CREATE TABLE t_user
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    user_id   VARCHAR(255) NOT NULL,
    user_name VARCHAR(255),
    tenant_id int          NOT NULL
);

CREATE TABLE t_order
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    user_id   VARCHAR(255) NOT NULL,
    order_id  VARCHAR(255) NOT NULL,
    tenant_id int          NOT NULL
);

-- INSERT INTO t_order ("user_id", "order_id", "tenant_id")
-- VALUES ('uid1', 'ord1', 1);
--
-- INSERT INTO t_user ("user_id", "tenant_id", "user_name")
-- VALUES ('uid3', 2, 'uname3');


-- INSERT INTO t_order ("id", "user_id", "order_id", "tenant_id")
-- VALUES (2, 'uid1', 'ord2', 1);
-- INSERT INTO t_order ("id", "user_id", "order_id", "tenant_id")
-- VALUES (3, 'uid3', 'ord1', 3);
-- INSERT INTO t_order ("id", "user_id", "order_id", "tenant_id")
-- VALUES (4, 'uid4', 'ord1', 4);
-- INSERT INTO t_order ("id", "user_id", "order_id", "tenant_id")
-- VALUES (5, 'uid2', 'ord1', 1);
-- INSERT INTO t_order ("id", "user_id", "order_id", "tenant_id")
-- VALUES (6, 'uid5', 'ord2', 5);

-- INSERT INTO t_user ("id", "user_id", "tenant_id", "user_name")
-- VALUES (4, 'uid4', 3, 'uname4');
-- INSERT INTO t_user ("id", "user_id", "tenant_id", "user_name")
-- VALUES (1, 'uid1', 1, 'uname1');
-- INSERT INTO t_user ("id", "user_id", "tenant_id", "user_name")
-- VALUES (2, 'uid2', 1, 'uname2');
-- INSERT INTO t_user ("id", "user_id", "tenant_id", "user_name")
-- VALUES (8, 'uid5', 4, 'uname5');
-- INSERT INTO t_user ("id", "user_id", "tenant_id", "user_name")
-- VALUES (9, 'uid6', 3, 'uname4');