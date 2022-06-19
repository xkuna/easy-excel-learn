package top.coolbreeze4j.easyexcellearn.read.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;
import top.coolbreeze4j.easyexcellearn.data.ComplexHeaderData2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CoolBreeze
 * @date 2022/6/18 20:40.
 * excel读取ComplexHeaderData2的监听器
 */
@Slf4j
public class ComplexHeaderData2Listener implements ReadListener<ComplexHeaderData2> {
    //数据集合
    private List<ComplexHeaderData2> cacheList = new ArrayList<>();
    /**
     * 这个每一条数据解析都会来调用
     */
    @Override
    public void invoke(ComplexHeaderData2 complexHeaderData, AnalysisContext context) {
        cacheList.add(complexHeaderData);
        System.out.println("invoke=> " + complexHeaderData);
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("cacheList共{}条数据", cacheList.size());
    }
}
