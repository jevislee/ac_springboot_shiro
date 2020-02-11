package com.mycloud.usermanage.mapper;

import com.mycloud.usermanage.entity.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Role record);

    @Select("select * from t_role order by name")
    @ResultMap("BaseResultMap")
    List<Role> queryAllRoles();

    @Select("select name from t_role where id in (select role_id from t_admin_role where admin_id = #{0})")
    List<String> queryRoleNamesByUserId(Integer userId);

    @Select("select count(*) from t_admin_role where role_id = #{0}")
    Integer queryRoleUseCount(Integer roleId);

    @Select("<script>SELECT t.id, t.name, a.id AS adminRoleId FROM t_role t " +
            "LEFT JOIN t_admin_role a ON a.role_id = t.id AND a.admin_id = #{adminId} " +
            "WHERE 1 = 1 " +
            "<if test='type == 1'> AND a.id IS NOT NULL </if>" +
            "<if test='type == 2'> AND a.id IS NULL </if>" +
            "ORDER BY adminRoleId DESC</script>")
    @ResultMap("BaseResultMap")
    List<Role> queryRoleForAdmin(@Param("adminId") Integer adminId, @Param("type") Integer type);
}
