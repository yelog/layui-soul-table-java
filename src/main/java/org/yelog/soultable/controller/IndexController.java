package org.yelog.soultable.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yelog.soultable.entity.Poetry;
import org.yelog.soultable.service.PoetryService;
import org.yelog.soultable.util.SoulPage;

@Controller
public class IndexController {

	@Autowired
	private PoetryService poetryService;

	@RequestMapping("/")
	public String index(Model model){
		return "index";
	}
	@RequestMapping("/index")
	public String index(){
		return "index";
	}


	/**
	 * 表格数据请求
	 *
	 * @author yelog
	 * @date 2019-08-01 17:03
	 * @param soulPage 接收表头筛选条件、分页条件
	 * @param poetry 接收定义的查询条件 （注意：没有自定义的表单条件，也建议写上，为了初始化 soulPage.obj ，用于反射获取 类信息）
	 * @return java.lang.Object
	 */
	@RequestMapping("/poetry/dataGrid")
	@ResponseBody
	public Object dataGrid(SoulPage<Poetry> soulPage, Poetry poetry) {
		soulPage.setObj(poetry);
		return poetryService.dataGrid(soulPage);
	}

}
