package top.coolbreeze4j.easyexcellearn.data;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author CoolBreeze
 * @date 2022/6/18 15:41.
 * demo数据实体类
 */
@Data
public class DemoData {
    @ExcelProperty(value = "姓名")
    private String name;
    @ExcelIgnore
    private int age;
    @ExcelProperty(value = "所在班级")
    private String clazz;
}
