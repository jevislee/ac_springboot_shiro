package com.mycloud.usermanage.mapper;

import com.mycloud.usermanage.entity.Admin;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AdminMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(Admin record);

    Admin selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Admin record);

    @Select("select id,name,pswd,status,last_login_time,create_time from t_admin where delete_time is null and name = #{0}")
    @ResultMap("BaseResultMap")
    Admin queryAdmin(String name);

    @Select("select id,name,status,last_login_time,create_time from t_admin where delete_time is null")
    @ResultMap("BaseResultMap")
    List<Admin> queryList(Admin param);
}
