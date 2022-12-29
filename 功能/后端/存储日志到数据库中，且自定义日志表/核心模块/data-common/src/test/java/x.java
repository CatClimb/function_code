import com.weipu.dx45gdata.common.entity.AbData;
import com.weipu.dx45gdata.common.mapper.AbDataMapper;
import com.weipu.dx45gdata.common.service.AbDataService;

import com.weipu.dx45gdata.common.xApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {xApplication.class})
public class x {
    @Resource
    AbDataMapper abDataMapper;

    @Resource
    AbDataService abDataService;
    @Test
    public void s(){
        abDataService.addAbData(null);
    }
}
