package com.weipu.dx45gdata.common.service.impl;


import com.weipu.dx45gdata.common.mapper.AbDataMapper;
import com.weipu.dx45gdata.common.service.AbDataService;
import com.weipu.dx45gdata.common.entity.AbData;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
@Slf4j
@Service
public class AbDataServiceImpl implements AbDataService {


    @Resource
    private AbDataMapper abDataMapper;



    @Override
    public boolean addAbData(AbData abData) {
        log.info("xxxxxxxxxxxxxxxxxxx");
        return abDataMapper.insert(abData);
    }

    @Override
    @Transactional
    public int addAbDataList(List<AbData> abDataList) {
        int count=0;
        for (int i = 0; i < abDataList.size(); i++) {
            boolean insert = abDataMapper.insert(abDataList.get(i));
            if(insert){
                count++;
            }
        }
        return count;
    }
}
