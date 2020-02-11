package com.mycloud.usermanage.mapper;

import com.mycloud.usermanage.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    @Select("<script>select id,name,nickname,mobile,email,sex,avatar_url,status,last_login_time,create_time<if test='queryPswd > 0'>,pswd</if> from t_user where delete_time is null and " +
            "<if test='userType == 1'>name = #{name}</if><if test='userType == 2'>wx_openid = #{name}</if><if test='userType == 3'>mobile = #{name}</if></script>")
    @ResultMap("BaseResultMap")
    User queryUser(@Param("name") String name, @Param("userType") Integer userType, @Param("queryPswd") Integer queryPswd);

    @Select("SELECT id,code from t_user where delete_time is null and mobile = #{0}")
    @ResultMap("BaseResultMap")
    User queryMobileUser(String mobile);

    @Select("select id,name,nickname,mobile,email,sex,avatar_url,status,last_login_time,create_time from t_user where delete_time is null")
    @ResultMap("BaseResultMap")
    List<User> queryList(User param);
}
