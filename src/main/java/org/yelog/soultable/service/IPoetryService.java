package org.yelog.soultable.service;

import org.yelog.soultable.entity.Poetry;
import org.yelog.soultable.util.SoulPage;

public interface IPoetryService {

    Object dataGrid(SoulPage<Poetry> soulPage);

}

