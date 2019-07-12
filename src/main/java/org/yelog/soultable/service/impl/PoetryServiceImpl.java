package org.yelog.soultable.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.yelog.soultable.mapper.PoetryMapper;
import org.yelog.soultable.entity.Poetry;
import org.yelog.soultable.entity.PoetrySo;
import org.yelog.soultable.service.IPoetryService;
import org.yelog.soultable.util.SoulPage;

@Service
public class PoetryServiceImpl extends ServiceImpl<PoetryMapper, Poetry> implements IPoetryService {

	@Override
	public Object dataGrid(SoulPage<Poetry> soulPage) {
		return soulPage.setData(baseMapper.dataGrid(soulPage,(PoetrySo) soulPage.getObj()));
	}

}