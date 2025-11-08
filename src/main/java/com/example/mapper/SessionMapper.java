package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.cache.MybatisRedisCache;
import com.example.domain.entity.Session;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@CacheNamespace(implementation = MybatisRedisCache.class)
public interface SessionMapper extends BaseMapper<Session> {

    @Select("select * from session where user_id = #{userId}")
    List<Session> getAllSessionId(Long userId);

}
