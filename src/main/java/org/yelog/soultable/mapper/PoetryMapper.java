package org.yelog.soultable.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.yelog.soultable.entity.Poetry;
import org.yelog.soultable.entity.PoetrySo;
import org.yelog.soultable.util.SoulPage;

import java.util.List;

@Mapper
public interface PoetryMapper {

    List<Poetry> dataGrid(SoulPage<Poetry> soulPage, @Param("so") PoetrySo poetrySo);

}
