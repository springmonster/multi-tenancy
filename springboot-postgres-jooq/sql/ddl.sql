--- CREATE USER TABLE ---
CREATE TABLE public.t_user
(
    id        serial4 NOT NULL,
    user_id   varchar NOT NULL,
    tenant_id int4    NOT NULL,
    user_name varchar NOT NULL,
    CONSTRAINT t_user_pk PRIMARY KEY (id)
);

--- CREATE ORDER TABLE ---
CREATE TABLE public.t_order
(
    id        serial4 NOT NULL,
    user_id   varchar NOT NULL,
    order_id  varchar NOT NULL,
    tenant_id int4    NOT NULL,
    CONSTRAINT t_order_pk PRIMARY KEY (id)
);

--- INSERT USER DATA ---
INSERT INTO public.t_user
    (user_id, tenant_id, user_name)
VALUES ('uid1', 1, 'uname1');

INSERT INTO public.t_user
    (user_id, tenant_id, user_name)
VALUES ('uid2', 1, 'uname2');

INSERT INTO public.t_user
    (user_id, tenant_id, user_name)
VALUES ('uid3', 2, 'uname3');

INSERT INTO public.t_user
    (user_id, tenant_id, user_name)
VALUES ('uid4', 3, 'uname4');

--- INSERT ORDER DATA ---
INSERT INTO public.t_order
    (user_id, order_id, tenant_id)
VALUES ('uid1', 'ord1', 1);

INSERT INTO public.t_order
    (user_id, order_id, tenant_id)
VALUES ('uid1', 'ord2', 1);

INSERT INTO public.t_order
    (user_id, order_id, tenant_id)
VALUES ('uid3', 'ord1', 3);

INSERT INTO public.t_order
    (user_id, order_id, tenant_id)
VALUES ('uid4', 'ord1', 4);

INSERT INTO public.t_order
    (user_id, order_id, tenant_id)
VALUES ('uid2', 'ord1', 1);
