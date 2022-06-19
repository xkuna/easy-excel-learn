package top.coolbreeze4j.easyexcellearn.data;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author CoolBreeze
 * @date 2022/6/19 10:11.
 * 多级表头数据模型
 */
@Data
public class ComplexHeaderData {
    @ExcelProperty({"统计","男"})
    private Integer manNum;
    @ExcelProperty({"统计","女"})
    private Integer womanNum;
    @ExcelProperty(index = 2)
    private String clazz;
}
