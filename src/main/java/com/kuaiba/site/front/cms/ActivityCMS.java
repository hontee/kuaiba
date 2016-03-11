package com.kuaiba.site.front.cms;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.kuaiba.site.core.CmsIds;
import com.kuaiba.site.core.utils.AjaxResponse;
import com.kuaiba.site.core.utils.AjaxUtils;
import com.kuaiba.site.core.utils.DataGrid;
import com.kuaiba.site.db.entity.Activity;
import com.kuaiba.site.db.entity.ActivityExample;
import com.kuaiba.site.front.co.BaseCO;
import com.kuaiba.site.service.utils.Pagination;

@Controller
@RequestMapping(CmsIds.CMS_ACTIVITIES)
public class ActivityCMS extends BaseCO {
	
	@RequiresRoles(value = "admin")
	@RequestMapping(value = CmsIds.HOME, method = RequestMethod.GET)
	public String index() {
		return "cms/activities/index";
	}

	@RequiresRoles(value = "admin")
	@RequestMapping(value = CmsIds.VIEW, method = RequestMethod.GET)
	public String view(@PathVariable Long id, Model model) {
		model.addAttribute("record", findByPrimaryKey(id));
		return "cms/activities/view";
	}

	@RequiresRoles(value = "admin")
	@RequestMapping(value = CmsIds.LIST)
	public @ResponseBody DataGrid<Activity> dataGrid(@RequestParam(required = false) String title, Pagination p) throws Exception {
		ActivityExample example = new ActivityExample();
		if (StringUtils.isNotBlank(title)) {
			example.createCriteria().andNameLike("%" + title + "%"); // 模糊查询
		}
		PageInfo<Activity> pageInfo = activityService.findByExample(example, p);
		return new DataGrid<>(pageInfo);
	}

	@RequiresRoles(value = "admin")
	@RequestMapping(value = CmsIds.DELETE, method = RequestMethod.POST)
	public @ResponseBody AjaxResponse delete(@PathVariable Long id) {
		activityService.deleteByPrimaryKey(id);
		return AjaxUtils.ok();
	}
	
	private Activity findByPrimaryKey(Long id) {
		return activityService.findByPrimaryKey(id);
	}

}