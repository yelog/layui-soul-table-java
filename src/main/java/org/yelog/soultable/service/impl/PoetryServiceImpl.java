package org.yelog.soultable.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yelog.soultable.dao.PoetryDao;
import org.yelog.soultable.entity.Poetry;
import org.yelog.soultable.service.PoetryService;
import org.yelog.soultable.util.SoulPage;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class PoetryServiceImpl extends BaseServiceImpl<Integer, Poetry> implements PoetryService {

	@Autowired
	public PoetryDao poetryDao;

	@Override
	public Object dataGrid(SoulPage<Poetry> soulPage) {

		// 拼接自己的业务逻辑
		StringBuilder sql = new StringBuilder();
		sql.append("select * from poetry where 1=1");

		// 获取表单条件（自己定义的 form 表单数据，没有可以不写）
		Poetry poetry = soulPage.getObj();
		Map<String, Object> param = new HashMap<>();
		if (poetry != null) {
			if (StringUtils.isNotBlank(poetry.getTitle())) {
				sql.append(" and title like :title");
				param.put("title", "%" + poetry.getTitle() + "%");
			}
			if (StringUtils.isNotBlank(poetry.getContent())) {
				sql.append(" and content like :content");
				param.put("content", "%" + poetry.getContent() + "%");
			}
			if (StringUtils.isNotBlank(poetry.getStartTime()) && StringUtils.isNotBlank(poetry.getEndTime())) {
				sql.append(" and create_time between :startTime and :endTime");
				param.put("startTime", poetry.getStartTime());
				param.put("endTime", poetry.getEndTime());
			}
		}
		// 拼接完成自己的业务sql，调用工具方法返回
		return poetryDao.soulDataGrid(soulPage, sql.toString(), param);
	}

}
