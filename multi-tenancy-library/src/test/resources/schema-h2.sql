CREATE SCHEMA IF NOT EXISTS PUBLIC;

DROP TABLE IF EXISTS t_user;
DROP TABLE IF EXISTS t_order;
DROP TABLE IF EXISTS t_order_detail;
DROP TABLE IF EXISTS t_department;
-- t_user
CREATE TABLE t_user
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    user_id   VARCHAR(255) NOT NULL,
    user_name VARCHAR(255),
    tenant_id INT          NOT NULL
);
-- t_order
CREATE TABLE t_order
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    user_id   VARCHAR(255) NOT NULL,
    order_id  VARCHAR(255) NOT NULL,
    tenant_id INT          NOT NULL
);
-- t_order_detail
CREATE TABLE t_order_detail
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    order_id  VARCHAR(255) NOT NULL,
    quantity  INT          NOT NULL,
    tenant_id INT          NOT NULL
);
-- t_department
CREATE TABLE t_department
(
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    department_id        INT          NOT NULL,
    department_name      VARCHAR(255) NOT NULL,
    parent_department_id INT,
    tenant_id            INT          NOT NULL
);