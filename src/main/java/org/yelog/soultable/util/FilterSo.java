package org.yelog.soultable.util;

import java.util.List;

/**
 * 表头筛选条件实体类
 *
 * @author yelog
 * @date 2019-01-02 10:00
 */
public class FilterSo {

	/**
	 * 唯一id
	 */
	private Long id;
	/**
	 * 前缀 and、or
	 */
	private String prefix;
	/**
	 * 模式 in、condition、date
	 */
	private String mode;
	/**
	 * 字段名
	 */
	private String field;
	/**
	 * 筛选类型
	 */
	private String type;
	/**
	 * 是否有分隔符
	 */
	private String split;
	/**
	 * 筛选值
	 */
	private String value;
	/**
	 * 筛选值
	 */
	private List<String> values;

	/**
	 * 子组数据
	 */
	private List<FilterSo> children;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public List<FilterSo> getChildren() {
		return children;
	}

	public void setChildren(List<FilterSo> children) {
		this.children = children;
	}
}
