package top.coolbreeze4j.easyexcellearn;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author CoolBreeze
 * @date 2022/6/20 20:46.
 */
@Data
public class DemoExtra {
    @ExcelProperty("第一列")
    private String cell1;
    @ExcelProperty("第二列")
    private String cell2;
}
