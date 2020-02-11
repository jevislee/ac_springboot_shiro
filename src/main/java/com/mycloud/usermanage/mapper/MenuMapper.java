package com.mycloud.usermanage.mapper;

import com.mycloud.usermanage.entity.Menu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MenuMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(Menu record);

    Menu selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Menu record);

    @Select("SELECT t.*, IFNULL(m.title,'无') AS parentTitle FROM t_menu t " +
            "LEFT JOIN t_menu m ON t.parent_id = m.id " +
            "ORDER BY t.parent_id,t.order_no")
    @ResultMap("BaseResultMap")
    List<Menu> queryAllMenu();

    @Select("select id,title from t_menu where parent_id = 0 and hidden = 0 order by order_no")
    @ResultMap("BaseResultMap")
    List<Menu> queryTopLevelMenu();

    @Select("select count(*) from t_menu where parent_id = 0 and id = #{0}")
    Integer isTopLevelMenu(Integer menuId);

    @Select("select count(*) from t_menu where parent_id = #{0}")
    Integer hasChildMenu(Integer menuId);

    @Select("SELECT t.*, IFNULL(m.title,'无') AS parentTitle FROM t_menu t " +
            "LEFT JOIN t_menu m ON t.parent_id = m.id " +
            "WHERE t.id IN (SELECT menu_id FROM t_role_menu WHERE role_id IN (SELECT role_id FROM t_admin_role WHERE admin_id = #{0})) " +
            "ORDER BY t.parent_id, t.order_no")
    @ResultMap("BaseResultMap")
    List<Menu> queryMenuByAdminId(Integer adminId);

    @Select("<script>SELECT t.*, IFNULL(m.title,'无') AS parentTitle, r.id AS roleMenuId FROM t_menu t " +
            "LEFT JOIN t_role_menu r ON r.menu_id = t.id AND r.role_id = #{roleId} " +
            "LEFT JOIN t_menu m ON t.parent_id = m.id " +
            "WHERE 1=1 " +
            "<if test='type == 1'> AND r.id IS NOT NULL </if>" +
            "<if test='type == 2'> AND r.id IS NULL </if>" +
            "<if test='title != null'> and t.title like CONCAT('%',#{title},'%') </if>" +
            "<if test='path != null'> and t.path like CONCAT('%',#{path},'%') </if>" +
            "ORDER BY roleMenuId DESC, t.parent_id, t.order_no</script>")
    @ResultMap("BaseResultMap")
    List<Menu> queryMenuForRole(@Param("roleId") Integer roleId, @Param("type") Integer type,
                              @Param("title") String title, @Param("path") String path);
}
