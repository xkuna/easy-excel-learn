package top.coolbreeze4j.easyexcellearn.read.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CoolBreeze
 * @date 2022/6/19 09:24.
 * 性别代码转换器
 */
@Slf4j
//这里的泛型是对应java属性类型
public class CustomSexConverter implements Converter<Integer> {
    @Override
    //要求java属性类型为Integer
    public Class<?> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    //要求excel字段为string
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 读取excel时会调用该方法
     * @return
     */
    @Override
    public Integer convertToJavaData(ReadConverterContext<?> context) throws Exception {
        log.info("转换器读取到excel数据:【{}】", context.getReadCellData().getStringValue());
        String sex = context.getReadCellData().getStringValue();
        switch (sex){
            case "男":
                return 1;
            case "女":
                return 2;
            default:
                return 0; //未知返回0
        }

    }

    /**
     * 这个是写excel，这里就默认父类规则
     */
    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Integer> context) throws Exception {
        return Converter.super.convertToExcelData(context);
    }
}
