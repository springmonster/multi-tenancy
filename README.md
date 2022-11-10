# Getting Started

## Description

- Same database, same schema, same table, filter by `tenant_id` column
- Auto extend SQL, add `where tenant_id in` or `and tenant_id in` conditions
- Check tenant_id related conditions when we `select`,`update`, `delete` tenant tables

## Technology

- Spring boot
- PostgreSQL
- Jooq

## Install PostgreSQL with Docker

```
```

## Create Table and Insert Data into Table

- execute sql in [ddl.sql](./springboot-postgres-jooq/sql/ddl.sql)

## Start application

- execute http request in [rest-api.http](./springboot-postgres-jooq/rest-api.http)

## Check result

## Configuration

```yaml
multi:
  tenancy:
    tenant-identifier: tenant_id
    tables:
      - public.t_user
      - public.t_order
    sql-check-filters-exist: true
    sql-auto-add-filters: true
```
