package com.ikyer.site.core.security;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikyer.site.core.exception.SecurityException;
import com.ikyer.site.db.entity.GlobalIDs;
import com.ikyer.site.db.entity.User;
import com.ikyer.site.rest.BaseService;

/**
 * 自定义用户Realm
 * 
 * @author larry.qi
 */
public class UserRealm extends AuthorizingRealm {

  private Logger logger = LoggerFactory.getLogger(UserRealm.class);

  private BaseService service;

  public void setService(BaseService service) {
    this.service = service;
  }

  /**
   * 登录成功的用户：角色和权限设置<br>
   * TODO: 由于SHIRO过滤器每次都会进行权限验证，Roles和Permissions最好使用缓存
   */
  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    logger.info("为登录成功的用户：{}，添加角色和权限", principals.getPrimaryPrincipal());
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    Set<String> roles = new HashSet<>();
    if (isAdmin()) { // 登录用户是否为管理员
      roles.add("admin");
      roles.add("user");
      info.setRoles(roles);
    } else {
      roles.add("user");
      info.setRoles(roles);
    }

    return info;
  }
  
  private boolean isAdmin() {
    Subject subject = SecurityUtils.getSubject();
    Session session = subject.getSession();
    Object obj = session.getAttribute(GlobalIDs.loginUser());

    if (obj instanceof User) {
      User user = (User) obj;
      return user.getType() == 2L;
    }
    return false;
  }

  /**
   * 用户身份验证
   */
  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
      throws AuthenticationException {
    String username = (String) token.getPrincipal();
    logger.info("用户身份验证：{}", username);

    User currentUser = null;
    try {
      currentUser = service.findUser(username);
    } catch (SecurityException e) {
      logger.info("用户名或密码错误：{}", e.getMessage());
    }

    if (currentUser == null) {
      throw new UnknownAccountException("用户名或密码错误");
    }
    // 授权登录
    return new SimpleAuthenticationInfo(currentUser.getName(), currentUser.getPassword(),
        ByteSource.Util.bytes(currentUser.getSalt()), getName());
  }

}
