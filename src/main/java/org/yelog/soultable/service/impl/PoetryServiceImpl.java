package org.yelog.soultable.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yelog.soultable.mapper.PoetryMapper;
import org.yelog.soultable.entity.Poetry;
import org.yelog.soultable.entity.PoetrySo;
import org.yelog.soultable.service.IPoetryService;
import org.yelog.soultable.util.SoulPage;

@Service
public class PoetryServiceImpl implements IPoetryService {

	@Autowired
	public PoetryMapper poetryMapper;

	@Override
	public Object dataGrid(SoulPage<Poetry> soulPage) {
		return soulPage.setData(poetryMapper.dataGrid(soulPage,(PoetrySo) soulPage.getObj()));
	}

}
