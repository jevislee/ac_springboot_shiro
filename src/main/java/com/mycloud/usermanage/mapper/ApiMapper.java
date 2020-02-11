package com.mycloud.usermanage.mapper;

import com.mycloud.usermanage.entity.Api;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ApiMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(Api record);

    Api selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Api record);

    @Select("<script>select * from t_api where 1=1 " +
            "<if test='title != null'> and title like CONCAT('%',#{title},'%') </if>" +
            "<if test='uri != null'> and uri like CONCAT('%',#{uri},'%') </if>" +
            "order by uri</script>")
    @ResultMap("BaseResultMap")
    List<Api> queryAllApi(@Param("title") String title, @Param("uri") String uri);

    @Select("select uri from t_api")
    List<String> queryApiUri();

    @Select("SELECT distinct uri FROM t_api WHERE id IN (SELECT api_id FROM t_role_api WHERE role_id IN (SELECT role_id FROM t_admin_role WHERE admin_id = #{0}))")
    List<String> queryApiPermNamesByAdminId(Integer adminId);

    @Select("SELECT uri FROM t_api WHERE id IN (SELECT api_id FROM t_role_api WHERE role_id = (SELECT id FROM t_role WHERE name = 'app'))")
    List<String> queryAppApiPermNames();

    @Select("<script>SELECT t.id, t.title, t.uri, r.id AS roleApiId FROM t_api t " +
            "LEFT JOIN t_role_api r ON r.api_id = t.id AND r.role_id = #{roleId} " +
            "WHERE 1=1 " +
            "<if test='type == 1'> AND r.id IS NOT NULL </if>" +
            "<if test='type == 2'> AND r.id IS NULL </if>" +
            "<if test='title != null'> and t.title like CONCAT('%',#{title},'%') </if>" +
            "<if test='uri != null'> and t.uri like CONCAT('%',#{uri},'%') </if>" +
            "ORDER BY roleApiId DESC, uri</script>")
    @ResultMap("BaseResultMap")
    List<Api> queryApiForRole(@Param("roleId") Integer roleId, @Param("type") Integer type,
                              @Param("title") String title, @Param("uri") String uri);
}
