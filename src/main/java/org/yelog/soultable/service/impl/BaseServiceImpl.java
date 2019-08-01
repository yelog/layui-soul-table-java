package org.yelog.soultable.service.impl;

import org.springframework.transaction.annotation.Transactional;
import org.yelog.soultable.dao.BaseDao;
import org.yelog.soultable.service.BaseService;

import java.io.Serializable;

@Transactional
public class BaseServiceImpl<PK extends Serializable,T> implements BaseService<PK ,T> {
	 private BaseDao<PK, T> baseDao;

    public BaseDao<PK, T> getBaseDao() {
        return baseDao;
    }

    public void setBaseDao(BaseDao<PK, T> baseDao) {
        this.baseDao = baseDao;
    }
}
