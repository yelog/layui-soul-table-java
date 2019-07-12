package org.yelog.soultable.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.yelog.soultable.entity.Poetry;
import org.yelog.soultable.entity.PoetrySo;
import org.yelog.soultable.util.SoulPage;

import java.util.List;

public interface PoetryMapper extends BaseMapper<Poetry> {

    List<Poetry> dataGrid(SoulPage<Poetry> soulPage, @Param("so") PoetrySo poetrySo);

}