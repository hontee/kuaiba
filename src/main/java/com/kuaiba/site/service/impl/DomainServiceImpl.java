package com.kuaiba.site.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kuaiba.site.core.cache.CacheIDs;
import com.kuaiba.site.core.cache.Memcacheds;
import com.kuaiba.site.core.exception.CreateException;
import com.kuaiba.site.core.exception.DeleteException;
import com.kuaiba.site.core.exception.ReadException;
import com.kuaiba.site.core.exception.SecurityException;
import com.kuaiba.site.core.exception.UpdateException;
import com.kuaiba.site.core.security.CurrentUser;
import com.kuaiba.site.db.dao.DomainMapper;
import com.kuaiba.site.db.entity.Attribute;
import com.kuaiba.site.db.entity.VUtil;
import com.kuaiba.site.db.entity.Domain;
import com.kuaiba.site.db.entity.DomainExample;
import com.kuaiba.site.db.entity.Pagination;
import com.kuaiba.site.front.vo.DomainVO;
import com.kuaiba.site.service.DomainService;

@Service
public class DomainServiceImpl implements DomainService {

	@Resource
	private DomainMapper mapper;
	
	@Override
	public PageInfo<Domain> search(DomainExample example, Pagination p) throws SecurityException { 
		try {
			VUtil.assertNotNull(example, p);
			PageHelper.startPage(p.getPage(), p.getRows(), p.getOrderByClause());
			List<Domain> list = this.read(example);
			return new PageInfo<>(list);
		} catch (Exception e) {
			throw new ReadException("分页读取领域失败", e);
		}
	}

	@Override
	public int count(DomainExample example) throws SecurityException { 
		try {
			VUtil.assertNotNull(example);
			return mapper.countByExample(example);
		} catch (Exception e) {
			throw new ReadException("统计领域失败", e);
		}
	}

	@Override
	public void delete(DomainExample example) throws SecurityException { 
		try {
			VUtil.assertNotNull(example);
			mapper.deleteByExample(example);
		} catch (Exception e) {
			throw new DeleteException("删除领域失败", e);
		}
	}

	@Override
	public void delete(Long id) throws SecurityException { 
		try {
			VUtil.assertNotNull(id);
			mapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new DeleteException("删除领域失败", e);
		}
	}

	@Override
	public void add(DomainVO vo) throws SecurityException { 
		try {
			VUtil.assertNotNull(vo);
			Domain record = new Domain();
			record.setCreator(CurrentUser.getCurrentUserName());
			record.setDescription(vo.getDescription());
			record.setName(vo.getName());
			record.setState(vo.getState());
			record.setTitle(vo.getTitle());
			record.setWeight(vo.getWeight());
			mapper.insert(record);
		} catch (Exception e) {
			throw new CreateException("添加领域失败", e);
		}
	}

	@Override
	public List<Domain> read(DomainExample example) throws SecurityException { 
		try {
			VUtil.assertNotNull(example);
			return mapper.selectByExample(example);
		} catch (Exception e) {
			throw new ReadException("读取领域失败", e);
		}
	}

	@Override
	public Domain read(Long id) throws SecurityException { 
		
		VUtil.assertNotNull(id);
		List<Domain> list = this.getDomains();
		
		for (Domain domain : list) {
			if (id.equals(domain.getId())) {
				return domain;
			}
		}
		
		return new Domain();
	}

	@Override
	public void update(Domain record, DomainExample example) throws SecurityException { 
		try {
			VUtil.assertNotNull(record, example);
			mapper.updateByExample(record, example);
		} catch (Exception e) {
			throw new UpdateException("更新领域失败", e);
		}
	}

	@Override
	public void update(Long id, DomainVO vo) throws SecurityException { 
		try {
			VUtil.assertNotNull(vo, id);
			Domain record = new Domain();
			record.setId(id);
			record.setDescription(vo.getDescription());
			record.setName(vo.getName());
			record.setState(vo.getState());
			record.setTitle(vo.getTitle());
			record.setWeight(vo.getWeight());
			mapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new UpdateException("更新领域失败", e);
		}
	}
	
	@Override
	public void update(Long id, int count) throws SecurityException { 
		try {
			VUtil.assertNotNull(id);
			Domain record = new Domain();
			record.setId(id);
			record.setCount((short) count);
			mapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new UpdateException("更新领域失败", e);
		}
	}

	@Override
	public List<Domain> search(DomainExample example) throws SecurityException { 
		try {
			VUtil.assertNotNull(example);
			return mapper.selectByCollect(example);
		} catch (Exception e) {
			throw new ReadException("读取领域失败", e);
		}
	}
	
	

	@Override
	public boolean validate(Attribute attr, String value) throws SecurityException {
		
		try {
			VUtil.assertNotNull(value);
			DomainExample example = new DomainExample();
			
			 if (attr == Attribute.TITLE) {
				example.createCriteria().andTitleEqualTo(value);
			} else { // Attribute.NAME
				example.createCriteria().andNameEqualTo(value);
			}
			
			return !mapper.selectByExample(example).isEmpty();
		} catch (Exception e) {
			throw new ReadException("验证领域" + attr.name() + "失败", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Domain> getDomains() throws SecurityException {
		
		List<Domain> list = new ArrayList<>();
		
		if (Memcacheds.exists(CacheIDs.DOMAINS)) {
			list = (List<Domain>) Memcacheds.get(CacheIDs.DOMAINS);
		} else {
			// 从数据库中获取
			list = this.read(new DomainExample());
			Memcacheds.set(CacheIDs.DOMAINS, 1000 * 60 * 30, list);
		}
		
		return list;
	}

}
