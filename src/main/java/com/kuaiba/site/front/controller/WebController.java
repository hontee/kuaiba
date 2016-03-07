package com.kuaiba.site.front.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.kuaiba.site.GlobalIds;
import com.kuaiba.site.HttpIds;
import com.kuaiba.site.db.entity.Bookmark;
import com.kuaiba.site.db.entity.BookmarkExample;
import com.kuaiba.site.db.entity.Category;
import com.kuaiba.site.db.entity.CategoryExample;
import com.kuaiba.site.db.entity.Domain;
import com.kuaiba.site.db.entity.DomainExample;
import com.kuaiba.site.db.entity.User;
import com.kuaiba.site.front.result.Result;
import com.kuaiba.site.front.result.ResultBuilder;
import com.kuaiba.site.service.kit.Pagination;

@Controller
public class WebController extends BaseController implements WebPage {
	
	@RequestMapping(value = HttpIds.ABOUT, method = RequestMethod.GET)
	@Override
	public String about() {
		return "views/about";
	}

	
	@RequestMapping(value = HttpIds.SEARCH, method = RequestMethod.GET)
	@Override
	public String search(@RequestParam String q, Pagination p, Model model) throws Exception {
		BookmarkExample example = new BookmarkExample();
		if (StringUtils.isNotEmpty(q)) {
			example.createCriteria().andTitleLike("%" + q + "q");
		}
		PageInfo<Bookmark> pageInfo = bookmarkService.findByExample(example, p);
		model.addAttribute("p", pageInfo);
		return "views/search";
	}
	
	@RequestMapping(value = HttpIds.LOGIN, method = RequestMethod.GET)
	@Override
	public String login() {
		boolean flag = (SecurityUtils.getSubject().getSession().getAttribute(GlobalIds.LOGIN_USER) != null);
		if (flag) {
			return redirect("/");
		}
		return "views/login";
	}
	
	@RequestMapping(value = HttpIds.LOGIN, method = RequestMethod.POST)
	@Override
	public @ResponseBody Result login(@RequestParam String username, @RequestParam String password) {
		try {
			userService.login(username, password);
			return ResultBuilder.ok();
		} catch (Exception e) { // 登录失败
			return ResultBuilder.failed(e);
		}
	}
	
	@RequestMapping(value = HttpIds.LOGOUT, method = RequestMethod.GET)
	@Override
	public String logout() {
		SecurityUtils.getSubject().logout();
		return redirect("/");
	}
	
	
	@RequestMapping(value = HttpIds.USER_HOME, method = RequestMethod.GET)
	@Override
	public String dashbord(@PathVariable String name) {
		return "views/users/index";
	}
	
	
	@RequestMapping(value = HttpIds.USER_PROFILE, method = RequestMethod.GET)
	@Override
	public String profilePage(@PathVariable String name) {
		return "views/users/profile";
	}
	
	
	@RequestMapping(value = HttpIds.USER_PROFILE, method = RequestMethod.POST)
	@Override
	public String profile(@PathVariable String name, User u) {
		return "views/users/profile";
	}
	
	@RequestMapping(value = HttpIds.GROUP, method = RequestMethod.GET)
	@Override
	public String group(Model model) {
		DomainExample oe = new DomainExample();
		oe.createCriteria().andStateEqualTo((byte)1);
		oe.setOrderByClause("weight DESC");
		List<Domain> orgs = domainService.findByCollect(oe);
		model.addAttribute("orgs", orgs);
		return "views/category";
	}
	
	@RequestMapping(value = HttpIds.GROUP_BY_ID, method = RequestMethod.GET)
	@Override
	public String groupById(@PathVariable Long id, Model model) {
		CategoryExample example = new CategoryExample();
		example.createCriteria().andIdEqualTo(id);
		List<Category> cates = categoryService.findByCollect(example);
		model.addAttribute("cates", cates);
		return "views/home";
	}
	
	@RequestMapping(value = HttpIds.HOME, method = RequestMethod.GET)
	@Override
	public String home(Pagination p, Model model) throws Exception {
		List<Category> cates = categoryService.findByCollect(new CategoryExample());
		model.addAttribute("cates", cates);
		return "views/home";
	}
	
	@RequestMapping(value = HttpIds.BOOKMARK_HIT, method = RequestMethod.GET)
	@Override
	public String hit(@PathVariable Long id, Model model) throws Exception {
		return redirect(bookmarkService.hit(id));
	}
	
	/**
	 * 分享站点
	 * @return
	 */
	@RequestMapping(value = HttpIds.RECOMMEND, method = RequestMethod.GET)
	@Override
	public String recommend() {
		return "views/recommend";
	}
	
	/**
	 * 分享站点
	 * @return
	 */
	@RequestMapping(value = HttpIds.RECOMMEND, method = RequestMethod.POST)
	@Override
	public @ResponseBody Result recommend(@RequestParam String url) {
		recommendService.add(url);
		return ResultBuilder.ok();
	}
	
	/**
	 * 添加关注
	 * @param id
	 * @return
	 */
	@RequiresRoles("user")
	@RequestMapping(value = HttpIds.BOOKMARK_FOLLOW, method = RequestMethod.POST)
	@Override
	public @ResponseBody Result bookmarkFollow(@PathVariable Long id) {
		bookmarkService.follow(id);
		return ResultBuilder.ok();
	}
	
	/**
	 * 取消关注
	 * @param id
	 * @return
	 */
	@RequiresRoles("user")
	@RequestMapping(value = HttpIds.BOOKMARK_UNFOLLOW, method = RequestMethod.POST)
	@Override
	public @ResponseBody Result bookmarkUnfollow(@PathVariable Long id) {
		bookmarkService.unfollow(id);
		return ResultBuilder.ok();
	}

	@RequiresRoles("user")
	@RequestMapping(value = HttpIds.GROUP_FOLLOW, method = RequestMethod.POST)
	@Override
	public @ResponseBody Result groupFollow(@PathVariable Long id) {
		groupService.follow(id);
		return ResultBuilder.ok();
	}

	@RequiresRoles("user")
	@RequestMapping(value = HttpIds.GROUP_UNFOLLOW, method = RequestMethod.POST)
	@Override
	public @ResponseBody Result groupUnfollow(@PathVariable Long id) {
		groupService.unfollow(id);
		return ResultBuilder.ok();
	}

}
