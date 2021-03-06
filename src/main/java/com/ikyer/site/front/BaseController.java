package com.ikyer.site.front;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.ui.Model;
import org.springframework.web.context.request.WebRequest;

import com.github.pagehelper.PageInfo;
import com.ikyer.site.core.exception.ErrorIDs;
import com.ikyer.site.core.exception.SecurityException;
import com.ikyer.site.db.entity.GlobalIDs;
import com.ikyer.site.db.entity.Pager;
import com.ikyer.site.db.entity.Pagination;
import com.ikyer.site.db.entity.Product;
import com.ikyer.site.db.entity.Topic;
import com.ikyer.site.db.entity.User;
import com.ikyer.site.front.vo.ResponseVO;

/**
 * 控制器基类
 * 
 * @author larry.qi
 *
 */
public abstract class BaseController {

  /**
   * 判断是否为AJAX请求
   * 
   * @param webRequest
   * @return
   */
  public boolean isAjaxRequest(WebRequest webRequest) {
    String requestedWith = webRequest.getHeader("X-Requested-With");
    return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
  }

  /**
   * 判断是否为AJAX上传
   * 
   * @param webRequest
   * @return
   */
  public boolean isAjaxUploadRequest(WebRequest webRequest) {
    return webRequest.getParameter("ajaxUpload") != null;
  }

  /**
   * 重定向
   * 
   * @param path
   * @return
   */
  public String redirect(String path) {
    return "redirect:".concat(path);
  }

  /**
   * 响应成功
   * 
   * @return
   */
  public ResponseVO buildResponse() {
    Response response = Response.ok().build();
    return new ResponseVO(response.getStatusInfo());
  }

  /**
   * 响应成功
   * 
   * @param entity
   * @return
   */
  public ResponseVO buildResponse(Object result) {
    Response response = Response.ok(result).build();
    return new ResponseVO(response.getEntity());
  }

  /**
   * 响应失败
   * 
   * @param message 错误描述
   * @param errorId 错误码
   * @return
   */
  public ResponseVO buildResponse(ErrorIDs errorId, String message) {
    return new ResponseVO(new SecurityException(errorId, message));
  }

  /**
   * 响应失败
   * 
   * @param e 异常类型
   * @return
   */
  public ResponseVO buildResponse(SecurityException e) {
    return new ResponseVO(e);
  }

  /**
   * 重置前端分页
   * 
   * @param p
   */
  public void initPagination(Pagination p) {
    p.setRows(GlobalIDs.frontRows());
  }
  
  /**
   * 重置前端分页
   * 
   * @param p
   */
  public void initPagination(Pagination p, String sort, String order) {
    initPagination(p);
    p.setOrderBy(sort, order);
  }

  /**
   * 获取Servlet请求路径，如果有参数会拼接参数
   * 
   * @param request
   * @return
   */
  public String getServletPath(HttpServletRequest request) {

    String servletPath = request.getServletPath();
    String query = request.getQueryString();

    if (StringUtils.isNotEmpty(query)) {
      servletPath = servletPath.concat("?").concat(query);
    }

    return servletPath;
  }

  /**
   * 初始信息
   * 
   * @param model
   * @param title
   * @throws SecurityException
   */
  public void addHeader(Model model, String title, HttpServletRequest request)
      throws SecurityException {
    model.addAttribute("title", title.concat(" · 快易开发者"));
    model.addAttribute("user", isAuthorized() ? getUserName() : null);
  }

  /**
   * 设置关键词
   * 
   * @param model
   * @param keywords
   * @throws SecurityException
   */
  public void addKeywords(Model model, String keywords, HttpServletRequest request)
      throws SecurityException {
    model.addAttribute("keywords", keywords);
  }
  
  /**
   * 设置描述内容
   * 
   * @param model
   * @param description
   * @throws SecurityException
   */
  public void addDescription(Model model, String description, HttpServletRequest request)
      throws SecurityException {
    model.addAttribute("description", description);
  }

  /**
   * 标识
   * 
   * @param model
   * @param f
   */
  public void addF(Model model, String f) {
    model.addAttribute("f", f);
  }

  /**
   * 查询关键字
   * 
   * @param model
   * @param q
   */
  public void addQ(Model model, String q) {
    model.addAttribute("q", q);
  }

  /**
   * ID
   * 
   * @param model
   * @param id
   */
  public void addId(Model model, Long id) {
    model.addAttribute("id", id);
  }
  
  /**
   * 用户
   * 
   * @param model
   * @param pageInfo
   * @param url
   */
  public void addUser(Model model, User user) {
    model.addAttribute("u", user);
  }

  /**
   * 记录
   * 
   * @param model
   * @param pageInfo
   * @param url
   */
  public void addRecord(Model model, Object record) {
    model.addAttribute("record", record);
  }

  /**
   * 分页
   * 
   * @param model
   * @param pageInfo
   * @param url
   */
  public void addPager(Model model, PageInfo<?> pageInfo, String url) {
    model.addAttribute("page", Pager.of(pageInfo, url));
  }

  /**
   * 产品列表
   * 
   * @param model
   * @param list
   */
  public void addProducts(Model model, List<Product> list) {
    model.addAttribute("products", list);
  }

  /**
   * 主题
   * 
   * @param model
   * @param topic
   */
  public void addTopic(Model model, Topic topic) {
    model.addAttribute("topic", topic);
  }

  /**
   * 主题列表
   * @param model
   * @param list
   */
  public void addTopics(Model model, List<Topic> list) {
    model.addAttribute("topics", list);
  }

  /**
   * 查询统计产品数和主题数
   * @param model
   * @param product 产品数
   * @param topic 主题数
   */
  public void addCount(Model model, long product, long topic) {
    model.addAttribute("product", product);
    model.addAttribute("topic", topic);
  }
  
  /**
   * 当前用户是否登录, 未登录返回false
   * @return
   */
  public boolean isAuthorized() {
    Subject subject = SecurityUtils.getSubject();
    Session session = subject.getSession();
    return session.getAttribute(GlobalIDs.loginUser()) != null;
  }

  /**
   * 获取当前登录用户, 未登录则返回 NULL
   * @return
   */
  public User getCurrentUser() {

    if (!isAuthorized()) {
      return new User(-1L, "anonymous");
    }

    Subject subject = SecurityUtils.getSubject();
    Session session = subject.getSession();
    return (User) session.getAttribute(GlobalIDs.loginUser());
  }

  /**
   * 获取当前登录用户名, 如果当前用户未登录则返回 "anonymous".
   * @return
   */
  public String getUserName() {
    return getCurrentUser().getName();
  }

  /**
   * 取得当前用户的登录id
   */
  public Long getUserId() {
    return getCurrentUser().getId();
  }

  /**
   * 用户是否为管理员, 未登录则返回false
   * @return
   */
  public boolean isAdmin() {
    return getCurrentUser().getType() == 2L;
  }

}
