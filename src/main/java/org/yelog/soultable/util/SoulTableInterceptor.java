package org.yelog.soultable.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.RowBounds;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *  tableFilter的mybatis拦截器
 *  支持：
 *  1、表头筛选
 *  2、分页
 *  3、目前支持数据库：mysql、oracle
 * @author Yelog
 * @date 2019-03-16 22:48
 * @version 1.0
 */
@Intercepts({@Signature(type= StatementHandler.class,method="prepare",args={Connection.class,Integer.class})})
public class SoulTableInterceptor implements Interceptor {
    public static Logger log = Logger.getLogger(SoulTableInterceptor.class);
    private String dbType;

	private enum DB_DIALECT {ORACLE, MYSQL};

    public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
        //通过MetaObject优雅访问对象的属性，这里是访问statementHandler的属性
        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        //先拦截到RoutingStatementHandler，里面有个StatementHandler类型的delegate变量，其实现类是BaseStatementHandler，然后就到BaseStatementHandler的成员变量mappedStatement
        MappedStatement mappedStatement = (MappedStatement)metaObject.getValue("delegate.mappedStatement");
        // 配置文件中SQL语句的ID
        BoundSql boundSql = statementHandler.getBoundSql();
        // 原始的SQL语句
        String sql = boundSql.getSql();
        // 检测未通过，不是select语句
        if (!checkIsSelectFalg(sql)) {
            return invocation.proceed();
        }
        if (boundSql.getParameterObject() instanceof Map || boundSql.getParameterObject() instanceof SoulPage) {
            SoulPage soulPage = null;
            if (boundSql.getParameterObject() instanceof SoulPage) {
                soulPage = (SoulPage) boundSql.getParameterObject();
            } else {
                Map<?,?> parameter = (Map<?,?>)boundSql.getParameterObject();
                for (Object key : parameter.keySet()) {
                    if (parameter.get(key) instanceof SoulPage) {
                        soulPage = (SoulPage) parameter.get(key);
                    }
                }
            }

            // 没有 soulPage 不需要拦截
            if(soulPage != null) {
                if (soulPage.isColumn()) {
                    // 排序
                    return invocation.proceed();
                } else {
                    Map<String, String> fieldMap = new HashMap<>();
                    if (mappedStatement.getResultMaps().get(0).getResultMappings().size()>0) {
                        for (ResultMapping resultMapping : mappedStatement.getResultMaps().get(0).getResultMappings()) {
                            fieldMap.put(resultMapping.getProperty(),resultMapping.getColumn());
                        }
                    }
                    /**
                     * 分页
                     */
                    StringBuffer filterSql = new StringBuffer("select * from (" + sql + ") A WHERE");
                    // 获取前端指定类型
                    Map<String, Map<String, String>> typeMap = soulPage.getTypeMap();
					List<FilterSo> filterSos = soulPage.getFilterSos();
					if (filterSos != null) {
                        filterSos.forEach(filterSo->{
                            handleFilterSo(filterSo, typeMap, fieldMap, filterSql);
                        });
                    }
					if (StringUtils.endsWith(filterSql, "WHERE")) {
					    filterSql.setLength(0);
					    filterSql.append("select * from (").append(sql).append(") A");
                    }

                    // 排序
                    if (StringUtils.isNotBlank(soulPage.getField())) {
                        filterSql.append(" order by ").append(fieldMap.size() > 0 ? fieldMap.get(soulPage.getField()) : soulPage.getField()).append(" ").append(soulPage.getOrder());
                    }

                    if (soulPage.getLimit()==100000000) {
                        metaObject.setValue("delegate.boundSql.sql",filterSql.toString());
                    } else {
                        // 设置总数
                        soulPage.setCount(getTotle(invocation, metaObject, filterSql.toString()));

                        // 改造后带分页查询的SQL语句 ORACLE 版
                        String pageSql;

                        if (DB_DIALECT.ORACLE.name().equalsIgnoreCase(dbType)) {
                            pageSql = "select * from (select * from ( select A.*,ROWNUM AS SOULROWNUM from ("
                                    + filterSql.toString() + " ) A) where SOULROWNUM <= " + (soulPage.getOffset() + soulPage.getLimit()) + ") where SOULROWNUM > " + soulPage.getOffset();
                        } else {
                            //改造后带分页查询的SQL语句 MYSQL版
                            pageSql = "select * from (" + filterSql.toString() + " ) A limit " + soulPage.getOffset() + ", " + soulPage.getLimit();
                        }
                        metaObject.setValue("delegate.boundSql.sql",pageSql);
                    }

                    // 采用物理分页后，就不需要mybatis的内存分页了，所以重置下面的两个参数
                    metaObject.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
                    metaObject.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
                }
            }
        }

        return invocation.proceed();
    }

    /**
     * 处理表头筛选数据
     *
     * @author Yelog
     * @date 2019-03-16 22:52
     * @param filterSo
     * @param typeMap
     * @param fieldMap
     * @param filterSql
     * @return void
     */
    private void handleFilterSo(FilterSo filterSo, Map<String, Map<String, String>> typeMap, Map<String, String> fieldMap, StringBuffer filterSql) {
        if (!StringUtils.endsWith(filterSql, "(") && !StringUtils.endsWith(filterSql, "WHERE")) {
            filterSql.append(StringUtils.isBlank(filterSo.getPrefix())?" and":" "+filterSo.getPrefix());
        }

        String field = fieldMap.size()>0? (fieldMap.get(filterSo.getField()) != null ? fieldMap.get(filterSo.getField()) : filterSo.getField()) :filterSo.getField();
        String value = filterSo.getValue();
        switch (filterSo.getMode()) {
            case "in":
                if (filterSo.getValues()==null || filterSo.getValues().size()==0) {
                    filterSql.append(" 1=1");
                    break;
                }
                switch (typeMap.get(field)==null?"":typeMap.get(field).get("type")) {
                    case "date":
						if (DB_DIALECT.ORACLE.name().equalsIgnoreCase(dbType)) {
                            filterSql.append(" to_char(");
						} else {
                            filterSql.append(" DATE_FORMAT(");
						}

						filterSql.append(field)
                                .append(", '");
                        if (DB_DIALECT.ORACLE.name().equalsIgnoreCase(dbType)) {
                            filterSql.append(typeMap.get(field).get("value").replaceAll("HH", "HH24").replaceAll("mm", "mi"));
                        } else {
                            filterSql.append(typeMap.get(field).get("value")
                                    .replaceAll("yyyy", "%Y")
                                    .replaceAll("MM", "%m")
                                    .replaceAll("dd", "%d")
                                    .replaceAll("HH", "%H")
                                    .replaceAll("mm", "%i")
                                    .replaceAll("ss", "%s"));
                        }

                        filterSql.append("') in ('")
                                .append(StringUtils.join(filterSo.getValues(), "','"))
                                .append("')");
                        break;
                    default:
                        if (StringUtils.isBlank(filterSo.getSplit())) {
                            filterSql.append(" ")
                                    .append(field)
                                    .append(" in ('")
                                    .append(StringUtils.join(filterSo.getValues(), "','"))
                                    .append("')");
                        } else {
                            //todo 兼容value值内包含正则特殊字符
                            if (DB_DIALECT.ORACLE.name().equalsIgnoreCase(dbType)) {
                                filterSql.append(" regexp_like(")
                                        .append(field)
                                        .append(", '");
                                for (String filterSoValue : filterSo.getValues()) {
                                    filterSql.append("("+filterSo.getSplit()+"|^){1}"+filterSoValue+"("+filterSo.getSplit()+"|$){1}|");
                                }
                                filterSql.deleteCharAt(filterSql.length()-1);
                                filterSql.append("')");
                            } else {
                                filterSql.append(" ")
                                        .append(field)
                                        .append(" regexp '(");
                                for (String filterSoValue : filterSo.getValues()) {
                                    filterSql.append("("+filterSo.getSplit()+"|^){1}"+filterSoValue+"("+filterSo.getSplit()+"|$){1}|");
                                }
                                filterSql.deleteCharAt(filterSql.length()-1);
                                filterSql.append(")+'");
                            }
                        }

                        break;
                }
                break;
            case "condition":
                if (StringUtils.isBlank(filterSo.getType()) || ((!"null".equals(filterSo.getType()) && !"notNull".equals(filterSo.getType())) && StringUtils.isBlank(filterSo.getValue()))) {
                    filterSql.append(" 1=1");
                    break;
                }
                filterSql.append(" ");
                filterSql.append(field);
                switch (filterSo.getType()) {
                    case "eq":
                        filterSql.append(" = '").append(value).append("'");
                        break;
                    case "ne":
                        filterSql.append(" != '").append(value).append("'");
                        break;
                    case "gt":
                        filterSql.append(" > '").append(value).append("'");
                        break;
                    case "ge":
                        filterSql.append(" >= '").append(value).append("'");
                        break;
                    case "lt":
                        filterSql.append(" < '").append(value).append("'");
                        break;
                    case "le":
                        filterSql.append(" <= '").append(value).append("'");
                        break;
                    case "contain":
                        filterSql.append(" like '%").append(value).append("%'");
                        break;
                    case "notContain":
                        filterSql.append(" not like '%").append(value).append("%'");
                        break;
                    case "start":
                        filterSql.append(" like '").append(value).append("%'");
                        break;
                    case "end":
                        filterSql.append(" like '%").append(value).append("'");
                        break;
                    case "null":
                        filterSql.append(" is null");
                        break;
                    case "notNull":
                        filterSql.append(" is not null");
                        break;
                    default:break;
                }
                break;
            case "date":
                filterSql.append(" ");
                filterSql.append(field);
                switch (filterSo.getType()) {
                    case "yesterday":
                        if (DB_DIALECT.ORACLE.name().equalsIgnoreCase(dbType)) {
                            filterSql.append(" between trunc(sysdate - 1) and trunc(sysdate)-1/(24*60*60) ");
                        } else {
                            filterSql.append(" between date_add(curdate(), interval -1 day) and date_add(curdate(),  interval -1 second) ");
                        }
                        break;
                    case "thisWeek":
                        if (DB_DIALECT.ORACLE.name().equalsIgnoreCase(dbType)) {
                            filterSql.append(" between trunc(sysdate - to_char(sysdate-2,'D')) and trunc(sysdate - to_char(sysdate-2,'D') + 7)-1/(24*60*60) ");
                        } else {
                            filterSql.append(" between date_add(curdate(), interval - weekday(curdate()) day) and date_add(date_add(curdate(), interval - weekday(curdate())+7 day), interval -1 second) ");
                        }
                        break;
                    case "lastWeek":
                        if (DB_DIALECT.ORACLE.name().equalsIgnoreCase(dbType)) {
                            filterSql.append(" between trunc(sysdate - to_char(sysdate-2,'D') - 7) and trunc(sysdate - to_char(sysdate-2,'D'))-1/(24*60*60) ");
                        } else {
                            filterSql.append(" between date_add(curdate(), interval - weekday(curdate())-7 day) and date_add(date_add(curdate(), interval - weekday(curdate()) day), interval -1 second) ");
                        }
                        break;
                    case "thisMonth":
                        if (DB_DIALECT.ORACLE.name().equalsIgnoreCase(dbType)) {
                            filterSql.append(" between trunc(sysdate, 'mm') and trunc(last_day(sysdate)+1)-1/(24*60*60) ");
                        } else {
                            filterSql.append(" between date_add(curdate(), interval - day(curdate()) + 1 day) and DATE_ADD(last_day(curdate()), interval 24*60*60-1 second) ");
                        }
                        break;
                    case "thisYear":
                        if (DB_DIALECT.ORACLE.name().equalsIgnoreCase(dbType)) {
                            filterSql.append(" between trunc(sysdate, 'yyyy') and to_date(to_char(sysdate,'yyyy')||'-12-31 23:59:59', 'yyyy-mm-dd hh24:mi:ss') ");
                        } else {
                            filterSql.append(" between date_sub(curdate(),interval dayofyear(now())-1 day) and str_to_date(concat(year(now()),'-12-31 23:59:59'), '%Y-%m-%d %H:%i:%s') ");
                        }

                        break;
                    case "specific":
                        if (DB_DIALECT.ORACLE.name().equalsIgnoreCase(dbType)) {
                            filterSql.append(" between to_date('").append(filterSo.getValue()).append("', 'yyyy-mm-dd') and to_date('").append(filterSo.getValue()).append("', 'yyyy-mm-dd')+1-1/(24*60*60) ");
                        } else {
                            filterSql.append(" between str_to_date('").append(filterSo.getValue()).append("', '%Y-%m-%d') and str_to_date(concat('").append(filterSo.getValue()).append("',' 23:59:59'), '%Y-%m-%d %H:%i:%s') ");
                        }
                        break;
                    case "all":
                    default:
                        filterSql.delete(filterSql.lastIndexOf(" "), filterSql.length());
                        filterSql.append(" 1=1");
                        break;
                }
                break;
            case "group":
                filterSql.append(" (");
                if (filterSo.getChildren().size()>0) {
                    filterSo.getChildren().forEach(f->{
                        handleFilterSo(f, typeMap, fieldMap ,filterSql);
                    });
                } else {
                    filterSql.append(" 1=1");
                }
                filterSql.append(" )");
            default:break;
        }


	}

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.dbType = properties.getProperty("dbType");
        if (StringUtils.isEmpty(dbType)) {
        	dbType = DB_DIALECT.ORACLE.name();
        }
    }

    /**
     * 获取当前sql查询的记录总数
     *
     * @author Yelog
     * @date 2019-03-16 22:53
     * @param invocation
     * @param metaObject
     * @param sql
     * @return int
     */
    private int getTotle(Invocation invocation, MetaObject metaObject, String sql) throws SQLException {
        Connection connection = (Connection)invocation.getArgs()[0];
        // 查询总条数的SQL语句
        String countSql = "select count(*) from (" + sql + ") a";
        //执行总条数SQL语句的查询
        PreparedStatement countStatement = connection.prepareStatement(countSql);
        ////获取参数信息即where语句的条件信息，注意上面拿到的sql中参数还是用?代替的
        ParameterHandler parameterHandler = (ParameterHandler) metaObject.getValue("delegate.parameterHandler");
        parameterHandler.setParameters(countStatement);
        ResultSet rs = countStatement.executeQuery();

        if(rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    /**
     * 判断是否是select语句，只有select语句，才会用到分页
     *
     * @author Yujie Yang
     * @date 2019-03-16 22:55
     * @param sql
     * @return boolean
     */
    private boolean checkIsSelectFalg(String sql) {
        String trimSql = sql.trim();
        int index = trimSql.toLowerCase().indexOf("select");
        return index == 0;
    }


}
