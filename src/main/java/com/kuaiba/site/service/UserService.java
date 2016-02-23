package com.kuaiba.site.service;

import java.util.List;

import com.kuaiba.site.db.entity.User;
import com.kuaiba.site.db.entity.UserExample;
import com.kuaiba.site.support.Pager;

public interface UserService extends Pager<User, UserExample> {
	
	int countByExample(UserExample example);

	void deleteByExample(UserExample example);

    void deleteByPrimaryKey(Long id);

    void add(User record);

    List<User> findByExample(UserExample example);

    User findByPrimaryKey(Long id);

    void updateByExample(User record, UserExample example);

    void updateByPrimaryKey(User record);

}
