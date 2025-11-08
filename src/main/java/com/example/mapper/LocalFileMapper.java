package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.example.cache.MybatisRedisCache;
import com.example.domain.entity.LocalFile;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@CacheNamespace(implementation = MybatisRedisCache.class)
public interface LocalFileMapper extends BaseMapper<LocalFile> {

}
