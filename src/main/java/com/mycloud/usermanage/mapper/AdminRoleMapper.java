package com.mycloud.usermanage.mapper;

import com.mycloud.usermanage.entity.AdminRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface AdminRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(AdminRole record);

    AdminRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AdminRole record);

    @Delete("delete from t_admin_role where admin_id = #{adminId} and role_id = #{roleId}")
    Integer deleteByAdminIdAndRoleId(@Param("adminId") Integer adminId, @Param("roleId") Integer roleId);

    @Delete("delete from t_admin_role where admin_id = #{adminId}")
    Integer deleteByAdminId(@Param("adminId") Integer adminId);
}
