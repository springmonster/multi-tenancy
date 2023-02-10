package com.example.multitenancylibrary.sql;

import com.example.multitenancylibrary.config.MultiTenancyProperties;
import com.example.multitenancylibrary.network.MultiTenancyStorage;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.QueryPart;
import org.jooq.Table;
import org.jooq.VisitContext;
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
import static org.jooq.Clause.DELETE;
import static org.jooq.Clause.DELETE_DELETE;
import static org.jooq.Clause.DELETE_WHERE;
import static org.jooq.Clause.INSERT;
import static org.jooq.Clause.SELECT;
import static org.jooq.Clause.SELECT_FROM;
import static org.jooq.Clause.SELECT_WHERE;
import static org.jooq.Clause.TABLE_ALIAS;
import static org.jooq.Clause.TABLE_JOIN_OUTER_LEFT;
import static org.jooq.Clause.TABLE_JOIN_OUTER_RIGHT;
import static org.jooq.Clause.UPDATE;
import static org.jooq.Clause.UPDATE_UPDATE;
import static org.jooq.Clause.UPDATE_WHERE;

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
        getWhereStack(context).pop();
        getConditionStack(context).pop();
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

                if (clauses.contains(TABLE_JOIN_OUTER_LEFT) ||
                        clauses.contains(TABLE_JOIN_OUTER_RIGHT)) {

                    if (clauses.contains(TABLE_ALIAS)) {
                        QueryPart[] parts = context.queryParts();

                        for (int i = parts.length - 2; i >= 0; i--) {
                            if (parts[i] instanceof Table) {
                                field = ((Table<?>) parts[i]).field(field);
                                break;
                            }
                        }
                    }

                    peekOns(context).add(field.in(values));
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
            pushConditionAndWhereAndOn(context);
        }
    }

    @Override
    public void clauseEnd(VisitContext context) {
        if (context.clause() == TABLE_JOIN_OUTER_LEFT ||
                context.clause() == TABLE_JOIN_OUTER_RIGHT) {
            QueryPart[] queryParts = context.queryParts();
            String rTableName = null;
            for (QueryPart queryPart : queryParts) {
                rTableName = getOuterJoinRightTableName(queryPart);
            }
            List<Condition> conditions = peekOns(context);

            if (!conditions.isEmpty()) {
                List<Condition> finalOnConditions = new ArrayList<>();
                for (Condition condition : conditions) {
                    if (null != rTableName && condition.toString().contains(rTableName)) {
                        finalOnConditions.add(condition);
                    }
                }
                getOnStack(context).poll();
                getOnStack(context).add(finalOnConditions);

                context.context()
                        .formatSeparator()
                        .keyword("and")
                        .sql(' ');

                List<Condition> collect = finalOnConditions.stream().distinct().toList();
                finalOnConditions.clear();
                finalOnConditions.addAll(collect);

                context.context().visit(DSL.condition(Operator.AND, finalOnConditions));
            }
        } else if (context.clause() == SELECT_WHERE ||
                context.clause() == UPDATE_WHERE ||
                context.clause() == DELETE_WHERE) {
            List<Condition> conditions = peekConditions(context);
            List<Condition> ons = peekOns(context);
            conditions.removeAll(ons);

            if (!conditions.isEmpty()) {
                context.context()
                        .formatSeparator()
                        .keyword(peekWhere(context) ? "and" : "where")
                        .sql(' ');

                List<Condition> collect = conditions.stream().distinct().toList();
                conditions.clear();
                conditions.addAll(collect);

                context.context().visit(DSL.condition(Operator.AND, conditions));
            }
        }

        if (context.clause() == SELECT ||
                context.clause() == UPDATE ||
                context.clause() == DELETE ||
                context.clause() == INSERT) {
            popConditionAndWhereAndOn(context);
        }
    }

    private String getOuterJoinRightTableName(QueryPart queryPart) {
        try {
            Class<?> joinTable = Class.forName("org.jooq.impl.JoinTable");
            if (joinTable.isAssignableFrom(queryPart.getClass())) {
                Object cast = joinTable.cast(queryPart);
                java.lang.reflect.Field rhs = ReflectionUtils.findField(joinTable, "rhs");
                ReflectionUtils.makeAccessible(rhs);
                Table rTable = (Table) ReflectionUtils.getField(rhs, cast);
                return rTable.getName();
            }

        } catch (ClassNotFoundException e) {
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