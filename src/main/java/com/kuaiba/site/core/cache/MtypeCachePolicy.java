package com.kuaiba.site.core.cache;

import java.util.List;

import com.kuaiba.site.core.exception.SecurityException;
import com.kuaiba.site.db.entity.Mtype;

/**
 * MType缓存接口
 * @author larry.qi
 *
 */
public interface MtypeCachePolicy {
	
	/**
	 * 获取所有MTypes
	 * @return
	 * @throws SecurityException
	 */
	public List<Mtype> getMtypes() throws SecurityException;
	
}