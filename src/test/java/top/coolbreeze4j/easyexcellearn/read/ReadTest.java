package top.coolbreeze4j.easyexcellearn.read;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.listener.ReadListener;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import top.coolbreeze4j.easyexcellearn.data.DemoData;
import top.coolbreeze4j.easyexcellearn.read.listener.DemoDataListener;

import java.io.IOException;

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
}
