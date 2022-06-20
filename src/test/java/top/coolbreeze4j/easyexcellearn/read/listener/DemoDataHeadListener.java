package top.coolbreeze4j.easyexcellearn.read.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ConverterUtils;
import lombok.extern.slf4j.Slf4j;
import top.coolbreeze4j.easyexcellearn.data.DemoData;

import java.util.Map;

/**
 * @author CoolBreeze
 * @date 2022/6/20 19:57.
 */
@Slf4j
public class DemoDataHeadListener implements ReadListener<DemoData> {
    /**
     * 解析过程中异常处理
     * 如果在该方法内抛出异常，则停止读取
     * 否则 继续读取下一行
     * @throws Exception
     */
    @Override
    public void onException(Exception exception, AnalysisContext context){
        log.info("解析失败，但继续读取下一行");
        //如果异常是 excel转换异常，那么打印 异常数据信息
        if(exception instanceof ExcelDataConvertException){
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
            log.error("第{}行，第{}列解析异常，数据为:{}", excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex(), excelDataConvertException.getCellData());
        }
    }

    /**
     * 每解析一个行头信息 执行一次该方法
     * @param headMap 一行的头信息
     */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        // 如果想转成成 Map<Integer,String> (cellIndex, cellName)
        // 方案1： 不要implements ReadListener 而是 extends AnalysisEventListener
        // 方案2： 调用 ConverterUtils.convertToStringMap(headMap, context) 自动会转换
        Map<Integer, String> headIndexAndInfo = ConverterUtils.convertToStringMap(headMap, context);
        log.info("一行excel头数据:{}", headIndexAndInfo);
    }

    /**
     * 每读取一行数据 执行一次该方法
     */
    @Override
    public void invoke(DemoData demoData, AnalysisContext analysisContext) {
        //略
    }

    /**
     * 数据读取完毕后 执行该方法
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //略
    }
}
