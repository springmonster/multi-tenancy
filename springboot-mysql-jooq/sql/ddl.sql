--- CREATE DATABASE ---
CREATE DATABASE `multi_tenancy`;

--- CREATE USER TABLE ---
CREATE TABLE multi_tenancy.t_user
(
    id        int auto_increment NOT NULL,
    user_id   varchar(100) NOT NULL,
    tenant_id int          NOT NULL,
    user_name varchar(100) NOT NULL,
    CONSTRAINT t_user_pk PRIMARY KEY (id)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;

--- CREATE ORDER TABLE ---
CREATE TABLE multi_tenancy.t_order
(
    id        int auto_increment NOT NULL,
    user_id   varchar(100) NOT NULL,
    order_id  varchar(100) NOT NULL,
    tenant_id int          NOT NULL,
    CONSTRAINT t_order_pk PRIMARY KEY (id)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;

--- INSERT USER DATA ---
INSERT INTO multi_tenancy.t_user
    (user_id, tenant_id, user_name)
VALUES ('uid1', 1, 'uname1');

INSERT INTO multi_tenancy.t_user
    (user_id, tenant_id, user_name)
VALUES ('uid2', 1, 'uname2');

INSERT INTO multi_tenancy.t_user
    (user_id, tenant_id, user_name)
VALUES ('uid3', 2, 'uname3');

INSERT INTO multi_tenancy.t_user
    (user_id, tenant_id, user_name)
VALUES ('uid4', 3, 'uname4');

--- INSERT ORDER DATA ---
INSERT INTO multi_tenancy.t_order
    (user_id, order_id, tenant_id)
VALUES ('uid1', 'ord1', 1);

INSERT INTO multi_tenancy.t_order
    (user_id, order_id, tenant_id)
VALUES ('uid1', 'ord2', 1);

INSERT INTO multi_tenancy.t_order
    (user_id, order_id, tenant_id)
VALUES ('uid3', 'ord1', 3);

INSERT INTO multi_tenancy.t_order
    (user_id, order_id, tenant_id)
VALUES ('uid4', 'ord1', 4);

INSERT INTO multi_tenancy.t_order
    (user_id, order_id, tenant_id)
VALUES ('uid2', 'ord1', 1);
