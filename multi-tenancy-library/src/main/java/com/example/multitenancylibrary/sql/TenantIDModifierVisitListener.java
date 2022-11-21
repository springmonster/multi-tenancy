package com.example.multitenancylibrary.sql;

import com.example.multitenancylibrary.config.MultiTenancyProperties;
import com.example.multitenancylibrary.network.MultiTenancyStorage;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultVisitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static java.util.Arrays.asList;
import static org.jooq.Clause.*;

@Component
public class TenantIDModifierVisitListener extends DefaultVisitListener {

    private MultiTenancyProperties multiTenancyProperties;

    @Autowired
    public TenantIDModifierVisitListener(MultiTenancyProperties multiTenancyProperties) {
        this.multiTenancyProperties = multiTenancyProperties;
    }

    private void pushConditionAndWhere(VisitContext context) {
        getConditionStack(context).push(new ArrayList<>());
        getWhereStack(context).push(false);
    }

    private void popConditionAndWhere(VisitContext context) {
        getWhereStack(context).pop();
        getConditionStack(context).pop();
    }

    private Deque<List<Condition>> getConditionStack(VisitContext context) {
        String conditions = "conditions";
        Deque<List<Condition>> data = (Deque<List<Condition>>) context.data(conditions);

        if (data == null) {
            data = new ArrayDeque<>();
            context.data(conditions, data);
        }

        return data;
    }

    private Deque<Boolean> getWhereStack(VisitContext context) {
        String predicates = "predicates";
        Deque<Boolean> data = (Deque<Boolean>) context.data(predicates);

        if (data == null) {
            data = new ArrayDeque<>();
            context.data(predicates, data);
        }

        return data;
    }

    private List<Condition> peekConditions(VisitContext context) {
        return getConditionStack(context).peek();
    }

    private boolean peekWhere(VisitContext context) {
        return getWhereStack(context).peek();
    }

    private void addWhere(VisitContext context, boolean value) {
        getWhereStack(context).pop();
        getWhereStack(context).push(value);
    }

    private <E> void addConditions(VisitContext context, Table<?> table, Field<E> field, E... values) {
        QueryPart queryPart = context.queryPart();

        if (queryPart instanceof Table) {
            Table queryTable = (Table) queryPart;
            if (queryTable.getName().equals(table.getName())) {
                List<Clause> clauses = getClauses(context);

                if (clauses.contains(SELECT_FROM) ||
                        clauses.contains(UPDATE_UPDATE) ||
                        clauses.contains(DELETE_DELETE)) {

                    if (clauses.contains(TABLE_ALIAS)) {
                        QueryPart[] parts = context.queryParts();

                        for (int i = parts.length - 2; i >= 0; i--) {
                            if (parts[i] instanceof Table) {
                                field = ((Table<?>) parts[i]).field(field);
                                break;
                            }
                        }
                    }

                    peekConditions(context).add(field.in(values));
                }
            }
        }
    }

    List<Clause> getClauses(VisitContext context) {
        List<Clause> result = asList(context.clauses());
        int index = result.lastIndexOf(SELECT);

        if (index > 0)
            return result.subList(index, result.size() - 1);
        else
            return result;
    }

    @Override
    public void clauseStart(VisitContext context) {
        if (context.clause() == SELECT ||
                context.clause() == UPDATE ||
                context.clause() == DELETE ||
                context.clause() == INSERT) {
            pushConditionAndWhere(context);
        }
    }

    @Override
    public void clauseEnd(VisitContext context) {
        if (context.clause() == SELECT_WHERE ||
                context.clause() == UPDATE_WHERE ||
                context.clause() == DELETE_WHERE) {
            List<Condition> conditions = peekConditions(context);

            if (conditions.size() > 0) {
                context.context()
                        .formatSeparator()
                        .keyword(peekWhere(context) ? "and" : "where")
                        .sql(' ');

                context.context().visit(DSL.condition(Operator.AND, conditions));
            }
        }

        if (context.clause() == SELECT ||
                context.clause() == UPDATE ||
                context.clause() == DELETE ||
                context.clause() == INSERT) {
            popConditionAndWhere(context);
        }
    }

    @Override
    public void visitEnd(VisitContext context) {
        addTenantInformation(context);

        if (context.queryPart() instanceof Condition) {
            List<Clause> clauses = getClauses(context);

            if (clauses.contains(SELECT_WHERE) ||
                    clauses.contains(UPDATE_WHERE) ||
                    clauses.contains(DELETE_WHERE)) {
                addWhere(context, true);
            }
        }
    }

    void addTenantInformation(VisitContext context) {
        Integer tenantID = MultiTenancyStorage.getTenantID();
        if (tenantID != null) {
            for (String table : multiTenancyProperties.getTables()) {
                String schemaName = table.split("\\.")[0];
                String tableName = table.split("\\.")[1];
                addConditions(context, DSL.table(DSL.name(tableName)),
                        DSL.field(DSL.name(schemaName, tableName, multiTenancyProperties.getTenantIdentifier())), tenantID);
            }
        }
    }
}