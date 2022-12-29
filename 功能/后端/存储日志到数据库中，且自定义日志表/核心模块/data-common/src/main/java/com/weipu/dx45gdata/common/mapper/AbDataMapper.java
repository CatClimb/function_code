package com.weipu.dx45gdata.common.mapper;

/*
 余子康
 */
import java.util.*;

import com.weipu.dx45gdata.common.entity.AbData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AbDataMapper {
//    public int insertBatch(@Param("abDataList") List<AbData> abDataList);
    public boolean insert(AbData abData);

}
