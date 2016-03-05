package com.kuaiba.site.db.dao;

import org.apache.ibatis.annotations.Param;

public interface GroupFollowMapper {
	
	int deleteByPrimaryKey(@Param("uid") Long uid, @Param("fid") Long fid);

	int insert(@Param("uid") Long uid, @Param("fid") Long fid);
}