package com.newtv.libs.db;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称:         DanceTv_Android
 * 包名:            com.newtv.dancetv.db
 * 创建事件:         12:45
 * 创建人:           weihaichao
 * 创建日期:          2018/2/24
 * <p>
 * eq               ==
 * noteq            !=
 * gt               >
 * lt               <
 * ge               >=
 * le               <=
 * like             包含
 * between          俩者之间
 * in               在某个值内
 * notIn            不在某个值内
 */

public class SqlCondition {
    //    private IdentityHashMap<String, String> conditionClause = new IdentityHashMap<>();
    private String orderBy = null;
    private String groupBy = null;
    private String limit = null;
    private String[] select = null;
    private boolean distinct = false;
    private SqlBuilder sqlBuilder;

    private StringBuilder clause = new StringBuilder();
    private List<String> args = new ArrayList<>();

    private SqlCondition(SqlBuilder builder) {
        sqlBuilder = builder;
    }

    static SqlCondition prepare(SqlBuilder sqlBuilder) {
        return new SqlCondition(sqlBuilder);
    }

    public SqlBuilder build() {
        if (sqlBuilder == null) {
            return SqlBuilder.create();
        }
        return sqlBuilder;
    }

    String[] getSelect() {
        return select;
    }

    String getOrderBy() {
        return orderBy;
    }

    String getGroupBy() {
        return groupBy;
    }

    String getLimit() {
        return limit;
    }

    Boolean getDistinct() {
        return distinct;
    }

    public String getClause() {
        return clause.toString();
    }

    String[] getArgs() {
        return args.toArray(new String[]{});
    }

    public SqlCondition select(String[] fields) {
        select = fields;
        return this;
    }

    public SqlCondition eq(String field, String... values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            builder.append(builder.length() > 0 ? " or " : "").append(field).append(" = ? ");
            args.add(value);
        }
        clause.append(builder);
        System.out.println(builder);
        System.out.println(args);
        return this;
    }

    public SqlCondition eq(String field, String value) {
        if (clause.length() > 0) {
            clause.append(" and ");
        }
        clause.append(field).append(" = ? ");
        args.add(value);
        return this;
    }

    public SqlCondition noteq(String field, String... values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            builder.append(builder.length() > 0 ? " or " : "").append(field).append(" != ? ");
            args.add(value);
        }
        clause.append(builder);
        System.out.println(builder);
        System.out.println(args);
        return this;
    }

    public SqlCondition noteq(String field, String value) {
        if (clause.length() > 0) {
            clause.append(" and ");
        }
        clause.append(field).append(" != ? ");
        args.add(value);
        return this;
    }

    public SqlCondition gt(String field, String value) {
        if (clause.length() > 0) {
            clause.append(" and ");
        }
        clause.append(field).append(" > ? ");
        args.add(value);
        return this;
    }


    public SqlCondition lt(String field, String value) {
        if (clause.length() > 0) {
            clause.append(" and ");
        }
        clause.append(field).append(" < ? ");
        args.add(value);
        return this;
    }


    public SqlCondition ge(String field, String value) {
        if (clause.length() > 0) {
            clause.append(" and ");
        }
        clause.append(field).append(" >= ? ");
        args.add(value);
        return this;
    }

    public SqlCondition le(String field, String value) {
        if (clause.length() > 0) {
            clause.append(" and ");
        }
        clause.append(field).append(" <= ? ");
        args.add(value);
        return this;
    }

    public SqlCondition like(String field, String value) {
        if (clause.length() > 0) {
            clause.append(" and ");
        }
        clause.append(field).append(field).append(" like ?");
        args.add("%" + value + "%");
        return this;
    }

    public SqlCondition OrderBy(String field) {
        orderBy = field;
        return this;
    }

    public SqlCondition groupBy(String value) {
        groupBy = value;
        return this;
    }

    public SqlCondition limit(String value) {
        limit = value;
        return this;
    }

    public SqlCondition Distinct(boolean vlaue) {
        distinct = vlaue;
        return this;
    }

}
