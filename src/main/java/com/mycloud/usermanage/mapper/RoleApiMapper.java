package com.mycloud.usermanage.mapper;

import com.mycloud.usermanage.entity.RoleApi;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface RoleApiMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(RoleApi record);

    RoleApi selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RoleApi record);

    @Delete("delete from t_role_api where role_id = #{roleId} and api_id = #{apiId}")
    Integer deleteByRoleIdAndApiId(@Param("roleId") Integer roleId, @Param("apiId") Integer apiId);

    @Select("select count(*) from t_role_api where api_id = #{0}")
    Integer queryApiUseCount(Integer apiId);
}
