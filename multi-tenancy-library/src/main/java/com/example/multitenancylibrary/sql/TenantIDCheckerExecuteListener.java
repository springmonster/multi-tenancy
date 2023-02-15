package com.example.multitenancylibrary.sql;

import com.example.multitenancylibrary.config.MultiTenancyProperties;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.jooq.ExecuteContext;
import org.jooq.impl.DefaultExecuteListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TenantIDCheckerExecuteListener extends DefaultExecuteListener {

    private final Logger logging = LoggerFactory.getLogger(TenantIDCheckerExecuteListener.class);
    private final MultiTenancyProperties multiTenancyProperties;

    @Autowired
    public TenantIDCheckerExecuteListener(MultiTenancyProperties multiTenancyProperties) {
        this.multiTenancyProperties = multiTenancyProperties;
    }

    private static final String SQL_PARSER_ERROR = "SQL parser error!";

    @Override
    public void renderEnd(ExecuteContext ctx) {
        String originalSQL = ctx.sql();

        if (originalSQL == null) {
            return;
        }

        logging.debug(originalSQL);

        checkSQL(originalSQL);
    }

    private void checkSQL(String originalSQL) {
        try {
            String transformOriginalSQL = transformOriginalSQL(originalSQL);
            Statement stmt = CCJSqlParserUtil.parse(transformOriginalSQL);
            if (stmt instanceof Select
                    || stmt instanceof Update
                    || stmt instanceof Delete) {
                checkSelectUpdateDeleteSQL(stmt, transformOriginalSQL);
            }
        } catch (JSQLParserException e) {
            throw new TenantIDException(SQL_PARSER_ERROR);
        }
    }

    private void checkSelectUpdateDeleteSQL(Statement statement, String originalSQL) {
        TablesFinder tablesFinder = new TablesFinder();
        tablesFinder.getTableList(statement);
        List<Table> tableList = tablesFinder.getTables();

        for (Table table : tableList) {
            boolean isTableExist = checkTable(table.getName());

            if (!isTableExist) {
                continue;
            }

            String tableName = getOriginalOrAliasTableName(table);

            boolean isConditionExist = isConditionExistInSQL(originalSQL, createEqCondition(tableName))
                    || isConditionExistInSQL(originalSQL, createInCondition(tableName));

            if (!isConditionExist) {
                throw new TenantIDException("Tenant conditions does not exist in table " + tableName);
            }
        }
    }

    private String createEqCondition(String tableName) {
        return tableName +
                "." +
                "\"" +
                this.multiTenancyProperties.getTenantIdentifier() +
                "\"" +
                " = ";
    }

    private String createInCondition(String tableName) {
        return tableName +
                "." +
                "\"" +
                this.multiTenancyProperties.getTenantIdentifier() +
                "\"" +
                " in ";
    }

    private String getOriginalOrAliasTableName(Table table) {
        Alias alias = table.getAlias();
        if (alias != null) {
            return alias.getName();
        }
        return table.getFullyQualifiedName();
    }

    private boolean isConditionExistInSQL(String sql, String condition) {
        return sql.contains(condition);
    }

    private boolean checkTable(String originalTableName) {
        boolean isTenantTableExist = false;
        for (String tableName : multiTenancyProperties.getTables()) {
            String tempTableName = tableName.split("\\.")[1];
            String transformedTempTableName = transformTableName(tempTableName);
            if (originalTableName.equalsIgnoreCase(transformedTempTableName)) {
                isTenantTableExist = true;
                break;
            }
        }
        return isTenantTableExist;
    }

    private String transformTableName(String tableName) {
        return "\"" +
                tableName +
                "\"";
    }

    private String transformOriginalSQL(String originalSQL) {
        if (originalSQL.contains("`")) {
            return originalSQL.replace("`", "\"");
        } else {
            return originalSQL;
        }
    }
}