-- CREATE POSTGRES USER
CREATE ROLE app_user LOGIN PASSWORD 'app_user';

-- GRANT PERMISSIONS TO APP_USER
GRANT
ALL
ON SCHEMA rls TO app_user;

GRANT
ALL
ON SEQUENCE rls.t_user_id_seq TO app_user;

GRANT ALL
ON TABLE rls.t_user TO app_user;

-- ENABLE ROW LEVEL SECURITY
ALTER TABLE rls.t_user ENABLE ROW LEVEL SECURITY;

-- CREATE POLICIES
CREATE
POLICY select_tenant_isolation_policy ON rls.t_user
    FOR
SELECT
    USING (tenant_id = current_setting('app.current_tenant'):: VARCHAR);

CREATE
POLICY update_tenant_isolation_policy ON rls.t_user
    FOR
UPDATE
    USING (tenant_id = current_setting('app.current_tenant'):: VARCHAR);

CREATE
POLICY delete_tenant_isolation_policy ON rls.t_user
    FOR DELETE
USING (tenant_id = current_setting('app.current_tenant')::VARCHAR);

CREATE
POLICY insert_tenant_isolation_policy ON rls.t_user
    FOR INSERT
    WITH CHECK(true);

-- CHECK ALL POLICIES
select *
from pg_policies;