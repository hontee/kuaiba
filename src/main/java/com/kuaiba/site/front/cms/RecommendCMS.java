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
import com.kuaiba.site.CmsIds;
import com.kuaiba.site.db.entity.Recommend;
import com.kuaiba.site.db.entity.RecommendExample;
import com.kuaiba.site.front.controller.BaseController;
import com.kuaiba.site.front.vo.BookmarkVO;
import com.kuaiba.site.front.vo.RecommendVO;
import com.kuaiba.site.service.kit.Pagination;
import com.kuaiba.site.utils.AjaxResponse;
import com.kuaiba.site.utils.AjaxUtils;
import com.kuaiba.site.utils.DataGrid;

@Controller
@RequestMapping(CmsIds.CMS_RECMDS)
public class RecommendCMS extends BaseController {
	
	@RequiresRoles(value = "admin")
	@RequestMapping(value = CmsIds.HOME, method = RequestMethod.GET)
	public String index() {
		return "cms/recmds/index";
	}

	@RequiresRoles(value = "admin")
	@RequestMapping(value = CmsIds.CREATE, method = RequestMethod.GET)
	public String addPage() {
		return "cms/recmds/new";
	}

	@RequiresRoles(value = "admin")
	@RequestMapping(value = CmsIds.EDIT, method = RequestMethod.GET)
	public String editPage(@PathVariable Long id, Model model) {
		model.addAttribute("record", findByPrimaryKey(id));
		return "cms/recmds/edit";
	}
	
	@RequiresRoles(value = "admin")
	@RequestMapping(value = CmsIds.AUDIT_OK, method = RequestMethod.GET)
	public String auditOKPage(@PathVariable Long id, Model model) {
		model.addAttribute("record", findByPrimaryKey(id));
		return "cms/recmds/audit-ok";
	}
	
	@RequiresRoles(value = "admin")
	@RequestMapping(value = CmsIds.AUDIT_NOT, method = RequestMethod.GET)
	public String auditNotPage(@PathVariable Long id, Model model) {
		model.addAttribute("record", findByPrimaryKey(id));
		return "cms/recmds/audit-not";
	}

	@RequiresRoles(value = "admin")
	@RequestMapping(value = CmsIds.VIEW, method = RequestMethod.GET)
	public String view(@PathVariable Long id, Model model) {
		model.addAttribute("record", findByPrimaryKey(id));
		return "cms/recmds/view";
	}

	@RequiresRoles(value = "admin")
	@RequestMapping(value = CmsIds.LIST)
	public @ResponseBody DataGrid<Recommend> dataGrid(@RequestParam(required = false) String title, Pagination p) throws Exception {
		RecommendExample example = new RecommendExample();
		if (StringUtils.isNotBlank(title)) {
			example.createCriteria().andTitleLike("%" + title + "%"); // 模糊查询
		}
		PageInfo<Recommend> pageInfo = recommendService.findByExample(example, p);
		return new DataGrid<>(pageInfo);
	}

	@RequiresRoles(value = "admin")
	@RequestMapping(value = CmsIds.CREATE, method = RequestMethod.POST)
	public @ResponseBody AjaxResponse add(@RequestParam String url) {
		recommendService.add(url);
		return AjaxUtils.ok();
	}

	@RequiresRoles(value = "admin")
	@RequestMapping(value = CmsIds.DELETE, method = RequestMethod.POST)
	public @ResponseBody AjaxResponse delete(@PathVariable Long id) {
		recommendService.deleteByPrimaryKey(id);
		return AjaxUtils.ok();
	}

	@RequiresRoles(value = "admin")
	@RequestMapping(value = CmsIds.EDIT, method = RequestMethod.POST)
	public @ResponseBody AjaxResponse edit(@PathVariable Long id, RecommendVO vo) {
		recommendService.updateByPrimaryKey(id, vo);
		return AjaxUtils.ok();
	}
	
	@RequiresRoles(value = "admin")
	@RequestMapping(value = CmsIds.AUDIT_OK, method = RequestMethod.POST)
	public @ResponseBody AjaxResponse auditOk(@PathVariable Long id, BookmarkVO vo) {
		recommendService.audit(id, vo);
		return AjaxUtils.ok();
	}
	
	@RequiresRoles(value = "admin")
	@RequestMapping(value = CmsIds.AUDIT_NOT, method = RequestMethod.POST)
	public @ResponseBody AjaxResponse auditNot(@PathVariable Long id, @RequestParam String remark) {
		recommendService.audit(id, remark);
		return AjaxUtils.ok();
	}
	
	private Recommend findByPrimaryKey(Long id) {
		return recommendService.findByPrimaryKey(id);
	}

}
