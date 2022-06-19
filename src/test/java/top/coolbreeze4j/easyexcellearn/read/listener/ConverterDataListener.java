package top.coolbreeze4j.easyexcellearn.read.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;
import top.coolbreeze4j.easyexcellearn.data.ConverterData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CoolBreeze
 * @date 2022/6/18 20:40.
 * excel读取ConverterData的监听器
 */
@Slf4j
public class ConverterDataListener implements ReadListener<ConverterData> {
    //数据集合
    private List<ConverterData> cacheList = new ArrayList<>();
    /**
     * 这个每一条数据解析都会来调用
     */
    @Override
    public void invoke(ConverterData converterData, AnalysisContext context) {
        cacheList.add(converterData);
        System.out.println("invoke=> " + converterData);
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("cacheList共{}条数据", cacheList.size());
    }
}
