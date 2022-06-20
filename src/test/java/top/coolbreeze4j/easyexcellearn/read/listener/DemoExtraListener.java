package top.coolbreeze4j.easyexcellearn.read.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import top.coolbreeze4j.easyexcellearn.DemoExtra;

/**
 * @author CoolBreeze
 * @date 2022/6/20 20:48.
 * excel（批注、超链接、合并单元格信息读取） 额外信息监听器
 */
@Slf4j
public class DemoExtraListener implements ReadListener<DemoExtra> {
    @Override
    public void invoke(DemoExtra demoExtra, AnalysisContext analysisContext) {
        //略
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //略
    }

    /**
     * 每读取到一条额外信息 执行一次该方法
     * @param extra
     * @param context
     */
    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        switch (extra.getType()){
            case COMMENT:
                log.info("额外信息是批注,在rowIndex:{},columnIndex;{},内容是:{}", extra.getRowIndex(), extra.getColumnIndex(),
                        extra.getText());
                break;
            case HYPERLINK:
                if ("Sheet1!H1".equals(extra.getText())) {
                    log.info("额外信息是超链接,在rowIndex:{},columnIndex;{},内容是:{}", extra.getRowIndex(),
                            extra.getColumnIndex(), extra.getText());
                } else if ("Sheet1!H2".equals(extra.getText())) {
                    log.info(
                            "额外信息是超链接,而且覆盖了一个区间,在firstRowIndex:{},firstColumnIndex;{},lastRowIndex:{},lastColumnIndex:{},",
                            extra.getFirstRowIndex(), extra.getFirstColumnIndex(), extra.getLastRowIndex(),
                            extra.getLastColumnIndex());
                } else {
                    Assert.fail("Unknown hyperlink!");
                }
                break;
            case MERGE:
                log.info(
                        "额外信息是合并单元格,而且覆盖了一个区间,在firstRowIndex:{},firstColumnIndex;{},lastRowIndex:{},lastColumnIndex:{}"
                                + "内容是:{}",
                        extra.getFirstRowIndex(), extra.getFirstColumnIndex(), extra.getLastRowIndex(),
                        extra.getLastColumnIndex(), extra.getText());
                break;
            default:
        }
    }
}
