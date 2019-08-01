package org.yelog.soultable.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.yelog.soultable.dao.BaseDao;
import org.yelog.soultable.util.SoulPage;
import org.yelog.soultable.util.SoulTableTool;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Map;

public class BaseDaoImpl<PK extends Serializable,T> implements BaseDao<PK,T> {
	@Resource
	protected SessionFactory sessionFactory;
	protected Session getSession(){
		return sessionFactory.getCurrentSession();
	}

	/**
	 *
	 * @param soulPage
	 * @param sql 自己的业务逻辑 sql
	 * @param param 自己业务逻辑 sql 中的条件参数
	 * @return
	 */
	@Override
	public Object soulDataGrid(SoulPage<T> soulPage, String sql, Map<String, Object> param) {
		return SoulTableTool.handle(getSession(), soulPage, sql, param, "MYSQL");
	}
}
