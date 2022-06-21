package top.coolbreeze4j.easyexcellearn.read.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author CoolBreeze
 * @date 2022/6/21 16:45.
 */
@Slf4j
public class MapReadListener extends AnalysisEventListener<Map<Integer, Object>> {

    /**
     * 每读取完一行 执行一次该方法
     * @param data key为列index, value为该行该列的数据
     * @param analysisContext
     */
    @Override
    public void invoke(Map<Integer, Object> data, AnalysisContext analysisContext) {
        log.info("读取到一行数据:\n{}", data);
    }

    /**
     * 全部读取完毕 执行该方法
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //略
    }
}
