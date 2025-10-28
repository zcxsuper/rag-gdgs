package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.cache.MybatisRedisCache;
import com.example.domain.entity.Message;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@CacheNamespace(implementation = MybatisRedisCache.class)
public interface MessageMapper extends BaseMapper<Message> {

    @Select("select * from message where session_id = #{sessionId} order by created asc")
    List<Message> findBySessionId(Long sessionId);
}
