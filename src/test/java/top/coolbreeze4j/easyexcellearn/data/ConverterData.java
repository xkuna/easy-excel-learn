package top.coolbreeze4j.easyexcellearn.data;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import top.coolbreeze4j.easyexcellearn.read.converter.CustomSexConverter;

/**
 * @author CoolBreeze
 * @date 2022/6/19 09:16.
 */
@Data
public class ConverterData {
    @ExcelProperty(value = "姓名")
    private String name;
    @ExcelProperty(value = "年龄")
    private Integer age;
    @ExcelProperty(value = "班级")
    private String clazz;
    @ExcelProperty(value = "性别",converter = CustomSexConverter.class)
    private Integer sex;
}
