package com.kuaiba.site.service;

import com.github.pagehelper.PageInfo;
import com.kuaiba.site.core.exception.SecurityException;
import com.kuaiba.site.db.entity.Bookmark;
import com.kuaiba.site.db.entity.BookmarkExample;
import com.kuaiba.site.db.entity.FollowUser;
import com.kuaiba.site.db.entity.FollowUserExample;
import com.kuaiba.site.db.entity.Pagination;

/**
 * 处理用户关注
 * @author larry.qi
 *
 */
public interface Followable {
	
	/**
	 * 根据条件统计站点被关注的用户数
	 * @param example
	 * @return
	 */
	int countBmfUser(FollowUserExample example) throws SecurityException;

	/**
	 * 分页查询
	 * @param example
	 * @param p
	 * @return PageInfo
	 * @throws SecurityException
	 */
	PageInfo<FollowUser> findBmfUser(FollowUserExample example, Pagination p) throws SecurityException;
	
	/**
	 * 分页查询
	 * @param example
	 * @param p
	 * @return PageInfo
	 * @throws SecurityException
	 */
	PageInfo<Bookmark> findGBRelation(BookmarkExample example, Pagination p) throws SecurityException;
	
	/**
	 * 根据条件统计群组被关注的用户数
	 * @param example
	 * @return
	 */
	int countGroupUser(FollowUserExample example) throws SecurityException;

	/**
	 * 分页查询
	 * @param example
	 * @param p
	 * @return PageInfo
	 * @throws SecurityException
	 */
	PageInfo<FollowUser> findGroupUser(FollowUserExample example, Pagination p) throws SecurityException;
	
}
