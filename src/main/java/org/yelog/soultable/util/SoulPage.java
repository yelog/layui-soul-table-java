package org.yelog.soultable.util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 封装table查询数据
 *
 * @author Yujie Yang
 * @date 2018/7/17 18:42
 * @return
 */
public class SoulPage<T> {

    /**
     * layui表格必须参数⬇⬇⬇⬇⬇⬇
     */
    private Integer code = 0;
    private String msg = "";
    /**
     * 总记录
     */
    private Integer count;
    /**
     * 显示的记录
     */
    private List<T> data;

    /**
     * 请求条件
     */
    @JsonIgnore
    private T obj;
    /**
     * 查询条件
     */
    @JsonIgnore
    private Map<String, Object> condition = new HashMap<>();

    /**
     * 请求参数⬇⬇⬇⬇⬇⬇
     */

    /**
     * 当前页 从1开始
     */
    @JsonIgnore
    private Integer page = 1;
    /**
     * 页大小
     */
    @JsonIgnore
    private Integer limit = 100000000;

    /**
     * 查询列数据
     */
    @JsonIgnore
    private String columns;

    /**
     * 表格列类型
     */
    @JsonIgnore
    private String tableFilterType;
    /**
     * 筛选信息
     */
    @JsonIgnore
    private String filterSos;

    /**
     * 排序信息
     */
    @JsonIgnore
    private String field;
    @JsonIgnore
    private String order = "asc";

    public SoulPage() {

    }

    public SoulPage(Integer page, Integer limit) {
        this.page = page;
        this.limit = limit;
    }

    public List<FilterSo> getFilterSos() {
        return JSON.parseArray(filterSos, FilterSo.class);
    }

    public void setFilterSos(String filterSos) {
        this.filterSos = filterSos;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<T> getData() {
        return data;
    }

    public Object setData(List<T> data) {
        if (isColumn()) {
            Map<String, Map<String, String>> typeMap = getTypeMap();
            Map<String, Set<String>> columnMap = new HashMap<>();
            for (T datum : data) {
                for (String column : getColumns()) {
                    columnMap.computeIfAbsent(column, k -> new HashSet<>());
                    Object columnObject = null;
                    if (datum instanceof Map) {
                        columnObject = ((Map<String, ?>) datum).get(column);
                    } else {
                        try {
                            columnObject = ReflectHelper.getValueByFieldName(datum, column);
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    if (columnObject != null) { //空值不展示
                        columnMap.get(column).add(getFormatValue(typeMap, column, columnObject));
                    }
                }
            }
            Map<String, List<String>> columnSortMap = new HashMap<>();
            Iterator<Map.Entry<String, Set<String>>> it = columnMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Set<String>> entry = it.next();
                ArrayList<String> list = new ArrayList<>(entry.getValue());
                columnSortMap.put(entry.getKey(), list);
            }
            return columnSortMap;
        } else {
            this.data = data;
            return this;
        }
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return (page - 1) * limit;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public List<String> getColumns() {
        return StringUtils.isNotBlank(columns) ? JSON.parseArray(columns, String.class) : new ArrayList<>();
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public String getTableFilterType() {
        return tableFilterType;
    }

    public void setTableFilterType(String tableFilterType) {
        this.tableFilterType = tableFilterType;
    }


    /**
     * 结构化 Filter type
     *
     * @return
     */
    @JsonIgnore
    public Map<String, Map<String, String>> getTypeMap() {
        Map<String, Map<String, String>> typeMap = new HashMap<>();
        if (StringUtils.isNotEmpty(tableFilterType)) {
            Map<String, String> filterType = JSON.parseObject(tableFilterType, Map.class);
            filterType.forEach((k, v) -> {
                Map<String, String> map = new HashMap<>();
                map.put("type", v.substring(0, v.indexOf("[")));
                map.put("value", v.substring(v.indexOf("[") + 1, v.indexOf("]")));
                typeMap.put(k, map);
            });
        }
        return typeMap;
    }

    /**
     * 根据类型转换最终的值
     *
     * @param typeMap
     * @param column
     * @param columnObject
     * @return
     */
    public String getFormatValue(Map<String, Map<String, String>> typeMap, String column, Object columnObject) {
        String columnValue;
        if (typeMap.containsKey(column)) {
            if ("date".equalsIgnoreCase(typeMap.get(column).get("type")) && columnObject instanceof Date) {
                columnValue = dateFormat((Date) columnObject, typeMap.get(column).get("value"));
            } else {
                columnValue = (String) columnObject;
            }
        } else {
            if (columnObject instanceof Date) {
                columnValue = dateFormat((Date) columnObject, null);
            } else {
                columnValue = String.valueOf(columnObject);
            }
        }
        return columnValue;
    }

    /**
     * 是否是列查询
     *
     * @return
     */
    public boolean isColumn() {
        return !getColumns().isEmpty();
    }

    public Map<String, Object> getCondition() {
        return condition;
    }

    public void setCondition(Map<String, Object> condition) {
        this.condition = condition;
    }

    private String dateFormat(Date date, String format) {
        if (StringUtils.isBlank(format)) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        } else {
            return new SimpleDateFormat(format).format(date);
        }
    }
}
