# Getting Started

## Technology

- Spring boot
- Postgresql
- Jooq

## Install Postgresql with Docker

```
docker run --name postgres -e POSTGRES_PASSWORD=123456 -e TZ=PRC -p 5432:5432 postgres:latest
```

## Application Implementation

- Same database, same schema, same table, filter by `tenant_id` column
- Auto extend SQL, add `where tenant_id in` or `and tenant_id in` conditions
- Check tenant_id related conditions when we `select`,`update`, `delete` tenant tables
- See [multi-tenancy-library](./multi-tenancy-library) and [springboot-postgres-jooq](./springboot-postgres-jooq)

### Create Table and Insert Data into Table

- Execute SQL in [ddl.sql](./springboot-postgres-jooq/sql/ddl.sql)

### Start Application

- Run [multi-tenancy-library](./multi-tenancy-library) publish task to make sure `multi-tenancy-library.jar` installed
  to maven local repository
- Start [springboot-postgres-jooq](./springboot-postgres-jooq) application

### Check Results

- Execute http request in [rest-api.http](./springboot-postgres-jooq/rest-api.http) to check results

### Configuration

```yaml
multi:
  tenancy:
    # enable multi tenancy
    enabled: true
    # tenant table tenant related column name
    tenant-identifier: tenant_id
    # tenant tables
    tables:
      - public.t_user
      - public.t_order
    # check tenant condition exist in SQL or not
    sql-check-filters-exist: true
    # auto add tenant condition to SQL
    sql-auto-add-filters: true
```

### Scheduler

- If there are SQL releated schedulers in application, we can not get tenantID
  through `request.getHeader("X-TenantID")` in `WebMvcConfig`
- We need to add `MultiTenancyStorage.setTenantID(tenantID)` before scheduler logic
  and add `MultiTenancyStorage.setTenantID(null)` after scheduler logic
- Please check `springboot-postgres-jooq/src/main/java/com/example/springbootpostgresjooq/job/ScheduledJob.java`
  for detailed information

## Row Level Security Implementation

- Same database, same schema, same table, filter by `tenant_id` column
- Using PostgreSQL Row Level Security
- See [rls](./rls)

### Create Table and Insert Data into Table

- Execute SQL in [ddl.sql](./rls/sql/ddl.sql)

### Enable RLS and Add Policies

- Execute SQL in [policies.sql](./rls/sql/policies.sql)

### Start Application

- Start [rls](./rls) application

### Check Results

- Execute http request in [rest-api.http](./rls/rest-api.http) to check results