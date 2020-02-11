package com.mycloud.usermanage.mapper;

import com.mycloud.usermanage.entity.RoleMenu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface RoleMenuMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(RoleMenu record);

    RoleMenu selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RoleMenu record);

    @Delete("delete from t_role_menu where role_id = #{roleId} and menu_id = #{menuId}")
    int deleteByRoleIdAndMenuId(@Param("roleId")Integer roleId, @Param("menuId") Integer menuId);

    @Select("SELECT SUM(c) FROM ( " +
            "SELECT COUNT(*) c FROM t_role_menu WHERE menu_id = #{0} " +
            "UNION " +
            "SELECT COUNT(*) c FROM t_menu WHERE parent_id = #{0} " +
            ") a")
    Integer queryMenuUseCount(Integer menuId);
}
