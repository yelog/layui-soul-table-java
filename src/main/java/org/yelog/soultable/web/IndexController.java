package org.yelog.soultable.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yelog.soultable.entity.Poetry;
import org.yelog.soultable.entity.PoetrySo;
import org.yelog.soultable.service.IPoetryService;
import org.yelog.soultable.util.SoulPage;

@Controller
public class IndexController {

	@Autowired
	private IPoetryService poetryService;

	@RequestMapping("/")
	public String index(Model model){
		return "index";
	}
	@RequestMapping("/index")
	public String index(){
		return "index";
	}

	@RequestMapping("/poetry/dataGrid")
	@ResponseBody
	public Object dataGrid(SoulPage<Poetry> soulPage, PoetrySo poetrySo) {
		soulPage.setObj(poetrySo);
		return poetryService.dataGrid(soulPage);
	}
}
