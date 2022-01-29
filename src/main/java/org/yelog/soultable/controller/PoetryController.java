package org.yelog.soultable.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yelog.soultable.entity.Poetry;
import org.yelog.soultable.entity.PoetrySo;
import org.yelog.soultable.service.IPoetryService;
import org.yelog.soultable.util.SoulPage;

@RestController
@RequestMapping("/poetry")
@CrossOrigin
public class PoetryController {

    @Autowired
    private IPoetryService poetryService;

    @RequestMapping("/dataGrid")
    public Object dataGrid(SoulPage<Poetry> soulPage, PoetrySo poetrySo) {
        soulPage.setObj(poetrySo);
        return poetryService.dataGrid(soulPage);
    }
}
