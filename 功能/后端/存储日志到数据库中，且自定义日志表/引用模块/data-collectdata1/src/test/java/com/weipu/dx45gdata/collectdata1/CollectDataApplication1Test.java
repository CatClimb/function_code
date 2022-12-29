package com.weipu.dx45gdata.collectdata1;


//import com.weipu.dx45gdata.common.CommonApplication;
//import com.weipu.dx45gdata.common.mapper.AbDataMapper;



//import com.weipu.dx45gdata.collectdata1.task.DownLoadTask;
import com.weipu.dx45gdata.collectdata1.task.DownLoadTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CollectDataApplication1.class, /*CommonApplication.class*/})
public class CollectDataApplication1Test {
//    @Resource
//    private AbDataMapper abDataMapper;
    @Resource
    private DownLoadTask downLoadTask;
    @Test
    public void test(){
        downLoadTask.downLoadCsv();
        System.out.println( );
    }
//    @Test
//    public void main() {
//        System.out.println(abDataMapper );
//    }
}