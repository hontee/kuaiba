package com.kuaiba.site.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kuaiba.site.core.exception.CreateException;
import com.kuaiba.site.core.exception.DeleteException;
import com.kuaiba.site.core.exception.FollowException;
import com.kuaiba.site.core.exception.ReadException;
import com.kuaiba.site.core.exception.SecurityException;
import com.kuaiba.site.core.exception.UnfollowException;
import com.kuaiba.site.core.exception.UpdateException;
import com.kuaiba.site.core.security.CurrentUser;
import com.kuaiba.site.db.dao.BookmarkFollowMapper;
import com.kuaiba.site.db.dao.BookmarkMapper;
import com.kuaiba.site.db.entity.Bookmark;
import com.kuaiba.site.db.entity.Bookmark.Attrs;
import com.kuaiba.site.db.entity.BookmarkExample;
import com.kuaiba.site.db.entity.ContraintValidator;
import com.kuaiba.site.db.entity.GlobalIDs;
import com.kuaiba.site.db.entity.HttpUtil;
import com.kuaiba.site.db.entity.Mtype;
import com.kuaiba.site.db.entity.Pagination;
import com.kuaiba.site.front.vo.BookmarkVO;
import com.kuaiba.site.service.BookmarkService;
import com.kuaiba.site.service.MtypeService;

@Service
public class BookmarkServiceImpl implements BookmarkService {
	
	@Resource
	private BookmarkMapper mapper;
	@Resource
	private BookmarkFollowMapper bfMapper;
	@Resource
	private MtypeService mtypeService;

	@Override
	public PageInfo<Bookmark> search(BookmarkExample example, Pagination p) throws SecurityException {
		try {
			ContraintValidator.checkNotNull(example, p);
			PageHelper.startPage(p.getPage(), p.getRows(), p.getOrderByClause());
			List<Bookmark> list = this.read(example);
			return new PageInfo<>(list);
		} catch (Exception e) {
			throw new ReadException("分页读取站点失败", e);
		}
	}

	@Override
	public int count(BookmarkExample example) throws SecurityException {
		try {
			ContraintValidator.checkNotNull(example);
			return mapper.countByExample(example);
		} catch (Exception e) {
			throw new ReadException("统计站点失败", e);
		}
	}

	@Override
	public void delete(BookmarkExample example) throws SecurityException { 
		try {
			ContraintValidator.checkNotNull(example);
			mapper.deleteByExample(example);
		} catch (Exception e) {
			throw new DeleteException("删除站点失败", e);
		}
	}

	@Override
	public void delete(Long id) throws SecurityException { 
		try {
			ContraintValidator.checkPrimaryKey(id);
			mapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new DeleteException("删除站点失败", e);
		}
	}

	@Override
	public void add(BookmarkVO vo) throws SecurityException { 
		try {
			ContraintValidator.checkNotNull(vo);
			Bookmark record = new Bookmark();
			record.setName(vo.getNameUUID());
			record.setTitle(vo.getTitle());
			record.setUrl(vo.getUrl());
			record.setDescription(vo.getDescription());
			record.setState(vo.getState());
			record.setCreateBy(CurrentUser.getCurrentUserId());
			record.setCategory(vo.getCategory());
			record.setHitRandom();
			record.setReffer(GlobalIDs.REFFER);
			mapper.insert(record);
		} catch (Exception e) {
			throw new CreateException("添加站点失败", e);
		}
	}

	@Override
	public List<Bookmark> read(BookmarkExample example) throws SecurityException { 
		try {
			ContraintValidator.checkNotNull(example);
			List<Bookmark> list = mapper.selectByExample(example);
			for (Bookmark bm : list) {
				Mtype mt = mtypeService.read(bm.getMtype());
				bm.setMt(mt);
			}
			return list;
		} catch (Exception e) {
			throw new ReadException("读取站点失败", e);
		}
	}

	@Override
	public Bookmark read(Long id) throws SecurityException { 
		try {
			ContraintValidator.checkPrimaryKey(id);
			return mapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ReadException("读取站点失败", e);
		}
	}

	@Override
	public void update(Bookmark record, BookmarkExample example) throws SecurityException { 
		try {
			ContraintValidator.checkNotNull(record, example);
			mapper.updateByExample(record, example);
		} catch (Exception e) {
			throw new UpdateException("更新站点失败", e);
		}
	}

	@Override
	public void update(Long id, BookmarkVO vo) throws SecurityException { 
		try {
			ContraintValidator.checkNotNull(vo);
			ContraintValidator.checkPrimaryKey(id);
			Bookmark record = new Bookmark();
			record.setId(id);
			record.setTitle(vo.getTitle());
			record.setUrl(vo.getUrl());
			record.setDescription(vo.getDescription());
			record.setState(vo.getState());
			record.setReffer(vo.getReffer());
			record.setCategory(vo.getCategory());
			mapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new UpdateException("更新站点失败", e);
		}
	}

	@Override
	public void unfollow(Long fid) throws SecurityException { 
		try {
			ContraintValidator.checkPrimaryKey(fid);
			bfMapper.deleteByPrimaryKey(CurrentUser.getCurrentUserId(), fid);
		} catch (Exception e) {
			throw new UnfollowException("取消关注站点失败", e);
		}
	}

	@Override
	public void follow(Long fid) throws SecurityException { 
		try {
			ContraintValidator.checkPrimaryKey(fid);
			bfMapper.insert(CurrentUser.getCurrentUserId(), fid);
		} catch (Exception e) {
			throw new FollowException("关注站点失败", e);
		}
	}

	@Override
	public String hit(Long id) throws SecurityException { 
		try {
			ContraintValidator.checkPrimaryKey(id);
			Bookmark record = read(id);
			record.setHit(record.getHit() + 1);
			mapper.updateByPrimaryKey(record);
			return HttpUtil.appendQueryParams(record.getUrl(), record.getReffer());
		} catch (Exception e) {
			throw new ReadException("点击获取URL失败", e);
		}
	}

	@Override
	public boolean isFollow(Long fid) throws SecurityException { 
		try {
			ContraintValidator.checkPrimaryKey(fid);
			List<Long> list = bfMapper.selectByUid(CurrentUser.getCurrentUserId());
			return list.contains(fid);
		} catch (Exception e) {
			throw new ReadException("判断用户是否关注站点失败", e);
		}
	}
	
	@Override
	public boolean validate(Attrs attr, String value) throws SecurityException {
		try {
			ContraintValidator.checkNotNull(value);
			BookmarkExample example = new BookmarkExample();
			
			if (attr == Bookmark.Attrs.NAME) {
				example.createCriteria().andNameEqualTo(value);
			} else if (attr == Bookmark.Attrs.TITLE) {
				example.createCriteria().andTitleEqualTo(value);
			} else if (attr == Bookmark.Attrs.URL) {
				example.createCriteria().andUrlEqualTo(value);
			}
			
			return !mapper.selectByExample(example).isEmpty();
		} catch (Exception e) {
			throw new ReadException("验证站点" + attr.name() + "失败", e);
		}
	}
	
}
