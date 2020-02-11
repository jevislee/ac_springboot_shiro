package com.mycloud.usermanage.mapper;

import com.mycloud.usermanage.entity.ExcludedApi;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ExcludedApiMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(ExcludedApi record);

    ExcludedApi selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ExcludedApi record);

    @Select("select * from t_excluded_api order by uri")
    @ResultMap("BaseResultMap")
    List<String> queryExcludedApi();

    @Select("select uri from t_excluded_api")
    List<String> queryExcludedApiUri();
}
