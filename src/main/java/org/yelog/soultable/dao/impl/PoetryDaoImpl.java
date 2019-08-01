package org.yelog.soultable.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.yelog.soultable.dao.PoetryDao;
import org.yelog.soultable.entity.Poetry;
import org.yelog.soultable.util.SoulPage;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Repository
public class PoetryDaoImpl extends BaseDaoImpl<Integer, Poetry> implements PoetryDao {

}