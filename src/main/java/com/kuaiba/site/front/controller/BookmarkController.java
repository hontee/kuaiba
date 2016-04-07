package com.kuaiba.site.front.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.kuaiba.site.core.exception.SecurityException;
import com.kuaiba.site.db.entity.Bookmark;
import com.kuaiba.site.db.entity.BookmarkExample;
import com.kuaiba.site.db.entity.Filter;
import com.kuaiba.site.db.entity.Pagination;
import com.kuaiba.site.db.entity.SiteResponse;
import com.kuaiba.site.service.BookmarkService;
import com.kuaiba.site.service.DomainService;
import com.kuaiba.site.service.RecommendService;

@Controller 
@Scope("prototype")
public class BookmarkController {
	
	@Resource
	private BookmarkService bookmarkService;
	@Resource
	private RecommendService recommendService;
	@Resource
	private DomainService ds;
	
	
	
	/**
	 * @WebPage 首页 = 我 | 猜你喜欢 | 全部
	 * 规则一：用户未登录则转发到 [发现页面] 
	 * 规则二：用户已登录则查询 [我 / 全部]
	 * @param p
	 * @param model
	 * @return
	 * @throws SecurityException
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String index(
			@RequestParam(required=false) String f,
			Pagination p, 
			Model model, 
			HttpServletRequest request) throws SecurityException {
		
		p.initFrontRows();
		Filter filter = Filter.parse(f);
		f = filter.toString().toLowerCase();
		
		PageInfo<Bookmark> pageInfo = new PageInfo<>();
		BookmarkExample example = new BookmarkExample();
		BookmarkExample.Criteria criteria = example.createCriteria();
		
		if (filter == Filter.MY) { // 我的站点
			pageInfo = bookmarkService.find(example, p);
		} else if (filter == Filter.LIKE) { // 猜你喜欢
			p.setOrderBy("hit", "DESC");
			pageInfo = bookmarkService.find(example, p);
		} else if (filter == Filter.NEW) { // 最新
			p.setOrderBy("created", "DESC");
			pageInfo = bookmarkService.find(example, p);
		} else if (filter == Filter.HOT) { // 最热
			p.setOrderBy("star", "DESC");
			pageInfo = bookmarkService.find(example, p);
		} else if (filter == Filter.PICK) { // 精选
			criteria.andPickEqualTo((byte)1);
			pageInfo = bookmarkService.find(example, p);
		}
		
		ModelUtil.addF(model, f);
		ModelUtil.addPager(model, pageInfo, "/?f=" + f);
		ModelUtil.addBookmarks(model, pageInfo.getList());
		ModelUtil.addHeader(model, "快吧 - 关注你喜欢的站点", ds);
		return "index.ftl";
	}
	
	/**
	 * @WebAPI 更新站点点击率和获取响应URL
	 * @param id
	 * @param model
	 * @return
	 * @throws SecurityException
	 */
	@RequestMapping(value = "/bookmarks/{id}/hit", method = RequestMethod.GET)
	public String hit(@PathVariable Long id, Model model) throws SecurityException {
		return SiteUtil.redirect(bookmarkService.updateHit(id));
	}
	

	/**
	 * @WebAPI 关注站点
	 * @param id
	 * @return
	 * @throws SecurityException 
	 */
	@RequiresRoles(value = {"user", "admin"})
	@RequestMapping(value = "/bookmarks/{id}/follow", method = RequestMethod.POST)
	public @ResponseBody SiteResponse follow(@PathVariable Long id) throws SecurityException {
		bookmarkService.follow(id);
		return SiteUtil.ok();
	}
	
	/**
	 * @WebAPI 取消关注站点
	 * @param id
	 * @return
	 * @throws SecurityException 
	 */
	@RequiresRoles(value = {"user", "admin"})
	@RequestMapping(value = "/bookmarks/{id}/unfollow", method = RequestMethod.POST)
	public @ResponseBody SiteResponse unfollow(@PathVariable Long id) throws SecurityException {
		bookmarkService.unfollow(id);
		return SiteUtil.ok();
	}
	
	/**
	 * @WebPage 分享站点
	 * @return
	 * @throws SecurityException 
	 */
	@RequestMapping(value = "/share", method = RequestMethod.GET)
	public String share(Model model) throws SecurityException {
		ModelUtil.addHeader(model, "分享你喜欢的站点 - 快吧", ds);
		return "share.ftl";
	}
	
	/**
	 * @WebAPI 分享站点
	 * @return
	 * @throws SecurityException 
	 */
	@RequestMapping(value = "/share", method = RequestMethod.POST)
	public @ResponseBody SiteResponse share(@RequestParam String url) throws SecurityException {
		recommendService.add(url);
		return SiteUtil.ok();
	}

}