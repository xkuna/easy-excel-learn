package top.coolbreeze4j.easyexcellearn.read;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import top.coolbreeze4j.easyexcellearn.DemoExtra;
import top.coolbreeze4j.easyexcellearn.data.ComplexHeaderData;
import top.coolbreeze4j.easyexcellearn.data.ComplexHeaderData2;
import top.coolbreeze4j.easyexcellearn.data.ConverterData;
import top.coolbreeze4j.easyexcellearn.data.DemoData;
import top.coolbreeze4j.easyexcellearn.read.listener.*;

import java.io.IOException;
import java.util.List;

/**
 * @author CoolBreeze
 * @date 2022/6/18 20:49.
 * easyexcel读取操作
 *
 */
public class ReadTest {
    @Test
    // 写法1：JDK8+ ,不用额外写一个DemoDataListener
    //版本要求 easyExcel>3.0  jdk8+
    public void simpleRead1() throws IOException {
        ClassPathResource excel = new ClassPathResource("excel/read/simpleRead.xlsx");
        //PageReadListener 是easyExcel已经实现的 一个Listener,每次100条数据然后返回
        EasyExcel.read(excel.getFile(), DemoData.class, new PageReadListener(data->{
            System.out.println(data);
        })).sheet().doRead();
    }
    @Test
    // 写法2：自己实现ReadListener
    public void simpleRead2() throws IOException {
        ClassPathResource excel = new ClassPathResource("excel/read/simpleRead.xlsx");
        //DemoDataListener是手动实现的一个ReadListener,需要重写 invoke() 和 doAfterAllAnalysed() 方法
        EasyExcel.read(excel.getFile(), DemoData.class, new DemoDataListener()).sheet().doRead();;
    }
    @Test
    //写法3：匿名内部类的方式实现ReadListener
    public void simpleRead3() throws IOException {
        ClassPathResource excel = new ClassPathResource("excel/read/simpleRead.xlsx");
        //DemoDataListener是手动实现的一个ReadListener,
        EasyExcel.read(excel.getFile(), DemoData.class, new ReadListener<DemoData>() {
            //每读取一行数据 执行一次该方法
            @Override
            public void invoke(DemoData demoData, AnalysisContext analysisContext) {
                System.out.println(demoData);
            }
            //excel读取完成后 执行该方法
            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                System.out.println("read excel end!");
            }
        }).sheet().doRead();;
    }

    @Test
    //指定读取一个sheet
    public void readBySheet1() throws IOException {
        ClassPathResource excel = new ClassPathResource("excel/read/simpleRead.xlsx");
        //DemoDataListener是手动实现的一个ReadListener,
        EasyExcel.read(excel.getFile(), DemoData.class, new DemoDataListener()).sheet(1).doRead();
    }

    @Test
    //指定读取多个sheet(sheet表头一致)
    public void readBySheet2() throws IOException {
        ClassPathResource excel = new ClassPathResource("excel/read/simpleRead.xlsx");
        //DemoDataListener是手动实现的一个ReadListener,
        ExcelReader excelReader = EasyExcel.read(excel.getFile(), DemoData.class, new DemoDataListener()).build();
        //构建sheet 这里可以指定名字或者no(也就是第几个sheet,比如 第一个sheet的no为0)
        //这里可以构建多个sheet,然后在下面read多个sheet(但是此时为多个sheet的表头一致，若不一致 需其他方法)
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        ReadSheet readSheet1 = EasyExcel.readSheet(1).build();

        // 读取sheet
        excelReader.read(readSheet,readSheet1);
    }

    @Test
    //读取全部sheet(sheet表头一致,不常用)
    public void readBySheet3() throws IOException {
        ClassPathResource excel = new ClassPathResource("excel/read/simpleRead.xlsx");
        //DemoDataListener是手动实现的一个ReadListener,
        EasyExcel.read(excel.getFile(), DemoData.class, new DemoDataListener()).doReadAll();
    }


    @Test
    //读取多个sheet(sheet表头可以不一致,常用)
    public void readBySheet4() throws IOException {
        ClassPathResource excel = new ClassPathResource("excel/read/simpleRead.xlsx");
        //DemoDataListener是手动实现的一个ReadListener,
        ExcelReader excelReader = EasyExcel.read(excel.getFile()).build();
        //这里为了方便 所以假定多个sheet的表头是一样的
        ReadSheet readSheet1 = EasyExcel.readSheet(0).head(DemoData.class).registerReadListener(new DemoDataListener()).build();
        ReadSheet readSheet2 = EasyExcel.readSheet(1).head(DemoData.class).registerReadListener(new DemoDataListener()).build();

        //最后开始读取
        //注意: 一定要把sheet1 sheet2 一起传进去，不然有个问题就是03版的excel 会读取多次，浪费性能
        excelReader.read(readSheet1, readSheet2);
    }

    @Test
    //读取时使用转换器(数据模型的某个字段进行转换)
    public void readByConverter() throws IOException {
        ClassPathResource excel = new ClassPathResource("excel/read/converterRead.xlsx");
        //ConverterDataListener是手动实现的一个ReadListener,
        //ConverterData 的sex设置了转换器，将填写的性别转换为Integer类型的代码
        //ConverterData 的sex @ExcelProperty 设置converter属性指定自定义转换器
        EasyExcel.read(excel.getFile(), ConverterData.class, new ConverterDataListener())
                .sheet().doRead();
    }


    @Test
    //读取多级表头(通过数据模型 @ExcelProperty注解实现)
    public void complexHeaderRead() throws IOException {
        ClassPathResource excel = new ClassPathResource("excel/read/complexHeader.xlsx");
        //ComplexHeaderDataListener是手动实现的一个ReadListener,
        //ComplexHeaderData中字段的 @ExcelProperty设置了多级表头,会自动识别
        //但是！！ 如果某个字段不是多级的 excel内是合并的单元格，那么@ExcelProperty设置value是读取不到的。
        //解决这种情况可以设置 @ExcelProperty的index
        EasyExcel.read(excel.getFile(), ComplexHeaderData.class, new ComplexHeaderDataListener())
                .sheet().doRead();
    }

    @Test
    //读取多级表头(读取时设置表头几行)
    public void complexHeaderRead2() throws IOException {
        ClassPathResource excel = new ClassPathResource("excel/read/complexHeader.xlsx");
        //ComplexHeaderData2Listener是手动实现的一个ReadListener,
        //需要注意同上的问题：如果某个字段不是多级的 excel内是合并的单元格，那么@ExcelProperty设置value是读取不到的。
        //解决这种情况可以设置 @ExcelProperty的index
        EasyExcel.read(excel.getFile(), ComplexHeaderData2.class, new ComplexHeaderData2Listener())
                //设置表头占了几行
                .headRowNumber(2)
                .sheet().doRead();
    }

    @Test
    //读取表头信息(不常用， 但设置出现异常不中断读取时，可以参考 DemoDataHeadListener重写的 onException() 方法)
    public void readHeader() throws IOException{
        ClassPathResource excel = new ClassPathResource("excel/read/simpleRead.xlsx");
        //DemoDataHeadListener是手动实现的一个ReadListener,
        //并且重写了 头信息读取方法 invokeHead() 和 解析异常方法 onException()
        EasyExcel.read(excel.getFile(), DemoData.class, new DemoDataHeadListener()).sheet().doRead();
    }

    @Test
    //读取额外信息（批注、超链接、合并单元格信息读取）
    public void readExtra() throws IOException {
        ClassPathResource excel = new ClassPathResource("excel/read/extra.xlsx");
        //DemoExtraListener是手动实现的一个ReadListener,
        //并且重写了 额外信息读取方法 extra()
        EasyExcel.read(excel.getFile(), DemoExtra.class, new DemoExtraListener())
                // 需要读取批注 默认不读取
                .extraRead(CellExtraTypeEnum.COMMENT)
                // 需要读取超链接 默认不读取
                .extraRead(CellExtraTypeEnum.HYPERLINK)
                // 需要读取合并单元格信息 默认不读取
                .extraRead(CellExtraTypeEnum.MERGE).sheet().doRead();
    }

    @Test
    //同步读取数据：等待读取完整个sheet 到一个list中，这样内存占用会非常大
    //不建议使用，大数据导入时，应实现一个ReadListener监听器 来 分批次list 入库，且每次入库后 清空内存中本次入库list
    public void syncRead() throws IOException {
        ClassPathResource excel = new ClassPathResource("excel/read/simpleRead.xlsx");
        List<DemoData> list = EasyExcel.read(excel.getFile()).head(DemoData.class).sheet().doReadSync();
        for (DemoData demoData : list) {
            System.out.println(demoData);
        }
    }

    @Test
    //读取数据为map
    public void readToMap() throws IOException {
        ClassPathResource excel = new ClassPathResource("excel/read/simpleRead.xlsx");
        //MapReadListener继承AnalysisEventListener
        //重写的 invoke() 方法中的 Map<Integer, Object> data，是列坐标 及该行该列的数据，可以根据业务在方法内再次组装
        EasyExcel.read(excel.getFile(), new MapReadListener()).sheet().doRead();
    }
}
