package org.yelog.soultable.dao;


import org.yelog.soultable.util.SoulPage;

import java.io.Serializable;
import java.util.Map;

public interface BaseDao<PK extends Serializable, T> {
	Object soulDataGrid(SoulPage<T> soulPage, String sql, Map<String, Object> param);
}
