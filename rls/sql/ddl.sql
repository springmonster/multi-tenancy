--- CREATE USER TABLE ---
CREATE TABLE rls.t_user
(
    id        serial4 NOT NULL,
    user_id   varchar NOT NULL,
    tenant_id varchar NOT NULL,
    user_name varchar NOT NULL,
    CONSTRAINT t_user_pk PRIMARY KEY (id)
);

--- INSERT USER DATA ---
INSERT INTO rls.t_user
    (user_id, tenant_id, user_name)
VALUES ('uid1', 1, 'uname1');

INSERT INTO rls.t_user
    (user_id, tenant_id, user_name)
VALUES ('uid2', 1, 'uname2');

INSERT INTO rls.t_user
    (user_id, tenant_id, user_name)
VALUES ('uid3', 2, 'uname3');

INSERT INTO rls.t_user
    (user_id, tenant_id, user_name)
VALUES ('uid4', 3, 'uname4');
