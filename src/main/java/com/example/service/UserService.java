package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.domain.dto.LoginDto;
import com.example.domain.dto.RegisterDto;
import com.example.domain.dto.UserUpdateDto;
import com.example.domain.po.User;
import com.example.domain.vo.UserInfoVo;
import com.example.exception.UnauthorizedException;
import com.example.exception.UserException;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.apache.ibatis.javassist.NotFoundException;

public interface UserService extends IService<User> {

    User findEnabledUserId(String email);

    User checkPassword(LoginDto loginDto);

    String register(RegisterDto registerDto) throws DatabaseException;

    void updateUserInfo(Long userId, UserUpdateDto userUpdateDto) throws UserException, DatabaseException;

    void removeUser(Long userId, Long id);

    PageDTO<UserInfoVo> getAllUserInfo(Integer pageNum, Integer pageSize);


}
