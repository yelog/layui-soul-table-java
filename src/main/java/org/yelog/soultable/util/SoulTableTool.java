package org.yelog.soultable.util;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.yelog.soultable.entity.Poetry;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * soulTable 筛选处理工具 hibernate 版
 *
 * @author yelog
 * @date 2019-08-01 18:06
 * @return
 */
public class SoulTableTool {

    private enum DB_DIALECT {ORACLE, MYSQL};

    /**
     *
     * @param session
     * @param soulPage
     * @param sql  自己业务的 sql
     * @param param 业务 sql 中的 条件参数
     * @param dbType 数据库类型支持两种 MySQL 与 ORACLE
     * @param <T>
     * @return
     */
    public static <T>Object handle(Session session, SoulPage<T> soulPage, String sql, Map param, String dbType){

        if (soulPage.isColumn()) {
            // 查询表头数据
            return soulPage.setData(session.createSQLQuery(sql).addEntity(Poetry.class).setProperties(param).list());
        } else {
            // 获取真正的 列名
            Map<String, String> fieldMap = new HashMap<>();
            Field[] fields = soulPage.getObj().getClass().getDeclaredFields();
            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if (column == null || StringUtils.isBlank(column.name())) {
                    fieldMap.put(field.getName(), field.getName());
                } else {
                    fieldMap.put(field.getName(), column.name());
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
                    handleFilterSo(filterSo, typeMap, filterSql, fieldMap, dbType);
                });
            }
            if (StringUtils.endsWith(filterSql, "WHERE")) {
                filterSql.setLength(0);
                filterSql.append("select * from (").append(sql).append(") A");
            }

            // 排序
            if (StringUtils.isNotBlank(soulPage.getField())) {
                filterSql.append(" order by ").append(fieldMap.get(soulPage.getField())).append(" ").append(soulPage.getOrder());
            }

            if (soulPage.getLimit()==100000000) {
                return soulPage.setData(session.createSQLQuery(sql).addEntity(Poetry.class).setProperties(param).list());
            } else {
                // 设置总数
                soulPage.setCount(((BigInteger)session.createSQLQuery("select count(*) from (" + filterSql.toString() + ") A").setProperties(param).uniqueResult()).intValue());

                // 查询当前页数据
                return soulPage.setData(session.createSQLQuery(filterSql.toString()).addEntity(Poetry.class).setProperties(param).setFirstResult(soulPage.getOffset()).setMaxResults(soulPage.getLimit()).list());
            }
        }
    }

    /**
     * 处理表头筛选数据
     *
     * @author Yelog
     * @date 2019-03-16 22:52
     * @param filterSo
     * @param typeMap
     * @param filterSql
     * @return void
     */
    private static void handleFilterSo(FilterSo filterSo, Map<String, Map<String, String>> typeMap, StringBuffer filterSql, Map<String, String> fieldMap, String dbType) {
        if (!StringUtils.endsWith(filterSql, "(") && !StringUtils.endsWith(filterSql, "WHERE")) {
            filterSql.append(StringUtils.isBlank(filterSo.getPrefix())?" and":" "+filterSo.getPrefix());
        }

        String field = fieldMap.size()>0?fieldMap.get(filterSo.getField()):filterSo.getField();
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
                        handleFilterSo(f, typeMap ,filterSql, fieldMap, dbType);
                    });
                } else {
                    filterSql.append(" 1=1");
                }
                filterSql.append(" )");
            default:break;
        }
	}

    /**
     * 获取obj对象fieldName的Field
     * @param obj
     * @param fieldName
     * @return
     */
    public static Field getFieldByFieldName(Object obj, String fieldName) {
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
            }
        }
        return null;
    }

    /**
     * 获取obj对象fieldName的属性值
     * @param obj
     * @param fieldName
     * @return
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Object getValueByFieldName(Object obj, String fieldName)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field field = getFieldByFieldName(obj, fieldName);
        Object value = null;
        if(field!=null){
            if (field.isAccessible()) {
                value = field.get(obj);
            } else {
                field.setAccessible(true);
                value = field.get(obj);
                field.setAccessible(false);
            }
        }
        return value;
    }
}
