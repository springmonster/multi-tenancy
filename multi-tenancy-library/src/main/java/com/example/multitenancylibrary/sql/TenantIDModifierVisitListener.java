package com.example.multitenancylibrary.sql;

import com.example.multitenancylibrary.config.MultiTenancyProperties;
import com.example.multitenancylibrary.network.MultiTenancyStorage;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultVisitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

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

    private void pushConditionAndWhereAndOn(VisitContext context) {
        getConditionStack(context).push(new ArrayList<>());
        getWhereStack(context).push(false);
        getOnStack(context).push(new ArrayList<>());
    }

    private void popConditionAndWhereAndOn(VisitContext context) {
        getConditionStack(context).pop();
        getWhereStack(context).pop();
        getOnStack(context).pop();
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

    private Deque<List<Condition>> getOnStack(VisitContext context) {
        String conditions = "on";
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

    private List<Condition> peekOns(VisitContext context) {
        return getOnStack(context).peek();
    }

    private boolean peekWheres(VisitContext context) {
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
            if (!queryTable.getName().equals(table.getName())) {
                return;
            }

            List<Clause> clauses = getClauses(context);

            if (clauses.contains(SELECT_FROM) ||
                    clauses.contains(UPDATE_UPDATE) ||
                    clauses.contains(DELETE_DELETE)) {
                field = getTableAlias(context, field, clauses);
                peekConditions(context).add(field.in(values));
            }
        }
    }

    private static <E> Field<E> getTableAlias(VisitContext context, Field<E> field, List<Clause> clauses) {
        if (clauses.contains(TABLE_ALIAS)) {
            QueryPart[] parts = context.queryParts();

            for (int i = parts.length - 2; i >= 0; i--) {
                if (parts[i] instanceof Table) {
                    field = ((Table<?>) parts[i]).field(field);
                    break;
                }
            }
        }
        return field;
    }

    List<Clause> getClauses(VisitContext context) {
        List<Clause> result = asList(context.clauses());
        int index = result.lastIndexOf(SELECT);

        if (index > 0) {
            return result.subList(index, result.size() - 1);
        } else {
            return result;
        }
    }

    @Override
    public void clauseStart(VisitContext context) {
        if (context.clause() == SELECT ||
                context.clause() == UPDATE ||
                context.clause() == DELETE ||
                context.clause() == INSERT) {
            pushConditionAndWhereAndOn(context);
        }
    }

    @Override
    public void clauseEnd(VisitContext context) {
        if (context.clause() == TABLE_JOIN_OUTER_LEFT) {
            autoExtendOuterJoin(context, true);
        } else if (context.clause() == TABLE_JOIN_OUTER_RIGHT) {
            autoExtendOuterJoin(context, false);
        } else if (context.clause() == SELECT_WHERE ||
                context.clause() == UPDATE_WHERE ||
                context.clause() == DELETE_WHERE) {
            autoExtendSelectUpdateDelete(context);
        }

        if (context.clause() == SELECT ||
                context.clause() == UPDATE ||
                context.clause() == DELETE ||
                context.clause() == INSERT) {
            popConditionAndWhereAndOn(context);
        }
    }

    private void autoExtendOuterJoin(VisitContext context, boolean isLeftOuterJoin) {
        List<Condition> conditions = peekConditions(context);
        if (conditions.isEmpty()) {
            return;
        }
        removeDuplicatedConditions(conditions);

        QueryPart[] queryParts = context.queryParts();
        String secondaryTable = null;
        for (QueryPart queryPart : queryParts) {
            secondaryTable = getOuterJoinSecondaryTableName(queryPart, isLeftOuterJoin);
        }

        List<Condition> finalRTableConditions = new ArrayList<>();
        for (Condition condition : conditions) {
            if (secondaryTable != null && condition.toString().contains(secondaryTable)) {
                finalRTableConditions.add(condition);
            }
        }

        if (!finalRTableConditions.isEmpty()) {
            peekOns(context).addAll(finalRTableConditions);

            context.context()
                    .formatSeparator()
                    .keyword("and")
                    .sql(' ');

            context.context().visit(DSL.condition(Operator.AND, finalRTableConditions));
        }
    }

    private void autoExtendSelectUpdateDelete(VisitContext context) {
        List<Condition> conditions = peekConditions(context);
        if (conditions.isEmpty()) {
            return;
        }
        removeDuplicatedConditions(conditions);

        List<Condition> ons = peekOns(context);
        conditions.removeAll(ons);

        if (!conditions.isEmpty()) {
            context.context()
                    .formatSeparator()
                    .keyword(peekWheres(context) ? "and" : "where")
                    .sql(' ');
            context.context().visit(DSL.condition(Operator.AND, conditions));
        }
    }

    private static void removeDuplicatedConditions(List<Condition> conditions) {
        List<Condition> collect = conditions.stream().distinct().toList();
        conditions.clear();
        conditions.addAll(collect);
    }

    private String getOuterJoinSecondaryTableName(QueryPart queryPart, boolean isLeftOuterJoin) {
        try {
            Class<?> joinTable = Class.forName("org.jooq.impl.JoinTable");
            if (joinTable.isAssignableFrom(queryPart.getClass())) {
                Object joinTableObj = joinTable.cast(queryPart);

                java.lang.reflect.Field table = ReflectionUtils.findField(joinTable, isLeftOuterJoin ? "rhs" : "lhs");
                ReflectionUtils.makeAccessible(table);
                return ((Table<?>) ReflectionUtils.getField(table, joinTableObj)).getName();
            }
        } catch (ClassNotFoundException ignored) {
        }
        return null;
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
        } else {
            throw new TenantIDException(multiTenancyProperties.getTenantIdentifier() + " value is missing! " +
                    "Please check MultiTenancyStorage.setTenantID(Integer) has invoked or not!");
        }
    }
}