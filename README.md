# EasyExcel

## 官方介绍

Java解析、生成Excel比较有名的框架有Apache poi、jxl。但他们都存在一个严重的问题就是非常的耗内存，poi有一套SAX模式的API可以一定程度的解决一些内存溢出的问题，但POI还是有一些缺陷，比如07版Excel解压缩以及解压后存储都是在内存中完成的，内存消耗依然很大。easyexcel重写了poi对07版Excel的解析，一个3M的excel用POI sax解析依然需要100M左右内存，改用easyexcel可以降低到几M，并且再大的excel也不会出现内存溢出；03版依赖POI的sax模式，在上层做了模型转换的封装，让使用者更加简单方便

快速、简洁、解决大文件内存溢出的java处理Excel工具

**github**:[https://github.com/alibaba/easyexcel](https://github.com/alibaba/easyexcel)



> tip:官方文档较为简介，可以参考官方github仓库中的测试demo,常见的使用场景都有介绍

**官方测试demo:**[https://github.com/alibaba/easyexcel/tree/master/easyexcel-test](https://github.com/alibaba/easyexcel/tree/master/easyexcel-test)





## 依赖

```xml
<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>easyexcel</artifactId>
  <version>3.0.5</version>
</dependency>
```



## 对象注解

> @ExcelProperty

用于匹配excel和实体类的匹配,参数如下：

| 名称      | 默认值            | 描述                                                         |
| --------- | ----------------- | ------------------------------------------------------------ |
| value     | 空                | 用于匹配excel中的头，必须全匹配,如果有多行头，会匹配最后一行头 |
| order     | Integer.MAX_VALUE | 优先级高于`value`，会根据`order`的顺序来匹配实体和excel中数据的顺序 |
| index     | -1                | 优先级高于`value`和`order`，会根据`index`直接指定到excel中具体的哪一列 |
| converter | 自动选择          | 指定当前字段用什么转换器，默认会自动选择。读的情况下只要实现`com.alibaba.excel.converters.Converter#convertToJavaData(com.alibaba.excel.converters.ReadConverterContext<?>)` 方法即可 |

> @ExcelIgnore

默认不加`@ExcelProperty` 的注解的都会参与读写，加了不会参与

## 读取excel

### 1.简单读

#### 数据模型

```java
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
```



#### excel

![image-20220619103743523](https://blog-1252734679.cos.ap-shanghai.myqcloud.com/markdown/image-20220619103743523.png)



#### ① 方式1(使用官方ReadListener)

```java
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
```



#### ② 方式2(手动实现ReadListener)

> ReadListener

```java
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;
import top.coolbreeze4j.easyexcellearn.data.DemoData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CoolBreeze
 * @date 2022/6/18 20:40.
 * excel读取DemoData的监听器
 */
@Slf4j
public class DemoDataListener implements ReadListener<DemoData> {
    //数据集合
    private List<DemoData> cacheList = new ArrayList<>();
    /**
     * 这个每一条数据解析都会来调用
     */
    @Override
    public void invoke(DemoData demoData, AnalysisContext context) {
        cacheList.add(demoData);
        System.out.println("invoke=> " + demoData);
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("cacheList共{}条数据", cacheList.size());
    }
}

```



> 读取Excel

```java
 @Test
    // 写法2：自己实现ReadListener
    public void simpleRead2() throws IOException {
        ClassPathResource excel = new ClassPathResource("excel/read/simpleRead.xlsx");
        //DemoDataListener是手动实现的一个ReadListener,需要重写 invoke() 和 doAfterAllAnalysed() 方法
        EasyExcel.read(excel.getFile(), DemoData.class, new DemoDataListener()).sheet().doRead();;
    }
```



#### ③ 方式3(匿名内部类实现ReadListener)

```java
@Test
    //写法3：匿名内部类的方式实现ReadListener
    public void simpleRead3() throws IOException {
        ClassPathResource excel = new ClassPathResource("excel/read/simpleRead.xlsx");
      	//需要重写 invoke() 和 doAfterAllAnalysed() 方法
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
```



### 2.根据sheet读

#### 数据模型

同上



#### excel

![](https://blog-1252734679.cos.ap-shanghai.myqcloud.com/markdown/image-20220619103904119.png)

![image-20220619103923481](https://blog-1252734679.cos.ap-shanghai.myqcloud.com/markdown/image-20220619103923481.png)



#### ① 指定读取一个sheet

```java
@Test
//指定读取一个sheet
public void readBySheet1() throws IOException {
  ClassPathResource excel = new ClassPathResource("excel/read/simpleRead.xlsx");
  //DemoDataListener是手动实现的一个ReadListener,
  EasyExcel.read(excel.getFile(), DemoData.class, new DemoDataListener()).sheet(1).doRead();
}
```



#### ② 指定读取sheet(sheet表头一致,不常用)

```java
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
```



#### ③ 读取全部sheet(sheet表头一致,不常用)

```java
@Test
//读取全部sheet(sheet表头一致,不常用)
public void readBySheet3() throws IOException {
  ClassPathResource excel = new ClassPathResource("excel/read/simpleRead.xlsx");
  //DemoDataListener是手动实现的一个ReadListener,
  EasyExcel.read(excel.getFile(), DemoData.class, new DemoDataListener()).doReadAll();
}
```



#### ④ 读取多个sheet(sheet表头可以不一致,常用)

```java
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
```

### 3.转换器

#### ① easy-excel自带转换器

> @DateTimeFormat

日期转换，用`String`去接收excel日期格式的数据会调用这个注解,参数如下：

| 名称             | 默认值   | 描述                                                         |
| ---------------- | -------- | ------------------------------------------------------------ |
| value            | 空       | 参照`java.text.SimpleDateFormat`书写即可                     |
| use1904windowing | 自动选择 | excel中时间是存储1900年起的一个双精度浮点数，但是有时候默认开始日期是1904，所以设置这个值改成默认1904年开始 |



> @NumberFormat

数字转换，用`String`去接收excel数字格式的数据会调用这个注解。

| 名称         | 默认值               | 描述                                  |
| ------------ | -------------------- | ------------------------------------- |
| value        | 空                   | 参照`java.text.DecimalFormat`书写即可 |
| roundingMode | RoundingMode.HALF_UP | 格式化的时候设置舍入模式              |

#### ② 自定义转换器

> 场景: 比如 填写一个excel时，表头的一个字段为性别。而数据库中性别存储的是性别代码
>
> 这样就需要 在excel导入且入库之前将性别进行转换

**数据模型**

```java
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
```



#### excel

![image-20220619111011874](https://blog-1252734679.cos.ap-shanghai.myqcloud.com/markdown/image-20220619111011874.png)



**自定义转换器**

```java
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

```



**数据模型监听器**

```java
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;
import top.coolbreeze4j.easyexcellearn.data.ConverterData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CoolBreeze
 * @date 2022/6/18 20:40.
 * excel读取ConverterData的监听器
 */
@Slf4j
public class ConverterDataListener implements ReadListener<ConverterData> {
    //数据集合
    private List<ConverterData> cacheList = new ArrayList<>();
    /**
     * 这个每一条数据解析都会来调用
     */
    @Override
    public void invoke(ConverterData converterData, AnalysisContext context) {
        cacheList.add(converterData);
        System.out.println("invoke=> " + converterData);
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("cacheList共{}条数据", cacheList.size());
    }
}

```



**实现代码**

```java
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
```



### 4.多行表头

####  ① 方式1(数据模型注解实现)

##### 数据模型

```java
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

```



##### excel

![image-20220619104629225](https://blog-1252734679.cos.ap-shanghai.myqcloud.com/markdown/image-20220619104629225.png)



##### 监听器

```java
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;
import top.coolbreeze4j.easyexcellearn.data.ComplexHeaderData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CoolBreeze
 * @date 2022/6/18 20:40.
 * excel读取ComplexHeaderData的监听器
 */
@Slf4j
public class ComplexHeaderDataListener implements ReadListener<ComplexHeaderData> {
    //数据集合
    private List<ComplexHeaderData> cacheList = new ArrayList<>();
    /**
     * 这个每一条数据解析都会来调用
     */
    @Override
    public void invoke(ComplexHeaderData complexHeaderData, AnalysisContext context) {
        cacheList.add(complexHeaderData);
        System.out.println("invoke=> " + complexHeaderData);
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("cacheList共{}条数据", cacheList.size());
    }
}

```



##### 实现代码

```java
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
```



#### ② 方式2(读取时设置表头几行)

##### 数据模型

```java
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author CoolBreeze
 * @date 2022/6/19 10:11.
 * 多级表头数据模型
 */
@Data
public class ComplexHeaderData2 {
    @ExcelProperty("男")
    private Integer manNum;
    @ExcelProperty("女")
    private Integer womanNum;
    @ExcelProperty(index = 2)
    private String clazz;
}

```



#### excel

同上



##### 监听器

```java
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;
import top.coolbreeze4j.easyexcellearn.data.ComplexHeaderData2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CoolBreeze
 * @date 2022/6/18 20:40.
 * excel读取ComplexHeaderData2的监听器
 */
@Slf4j
public class ComplexHeaderData2Listener implements ReadListener<ComplexHeaderData2> {
    //数据集合
    private List<ComplexHeaderData2> cacheList = new ArrayList<>();
    /**
     * 这个每一条数据解析都会来调用
     */
    @Override
    public void invoke(ComplexHeaderData2 complexHeaderData, AnalysisContext context) {
        cacheList.add(complexHeaderData);
        System.out.println("invoke=> " + complexHeaderData);
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("cacheList共{}条数据", cacheList.size());
    }
}
```



##### 实现代码

```java
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
```



### 5.获取excel头信息，并处理读取异常



#### 数据模型

```java
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

```



#### excel

![image-20220620201553650](https://blog-1252734679.cos.ap-shanghai.myqcloud.com/markdown/image-20220620201553650.png)



#### 监听器

```java

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

```



#### 实现代码

```java
@Test
//读取表头信息(不常用， 但设置出现异常不中断读取时，可以参考 DemoDataHeadListener重写的 onException() 方法)
public void readHeader() throws IOException{
  ClassPathResource excel = new ClassPathResource("excel/read/simpleRead.xlsx");
  //DemoDataHeadListener是手动实现的一个ReadListener,
  //并且重写了 头信息读取方法 invokeHead() 和 解析异常方法 onException()
  EasyExcel.read(excel.getFile(), DemoData.class, new DemoDataHeadListener()).sheet().doRead();
}
```





### 6.获取excel额外信息(批注 超链接 合并单元格)



#### 数据模型

```java
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

```



#### excel

![image-20220620210953467](https://blog-1252734679.cos.ap-shanghai.myqcloud.com/markdown/image-20220620210953467.png)



#### 监听器

```java
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

```



#### 实现代码

```java
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
```



### 7.同步读取数据(不推荐使用)



#### 数据模型

```java
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
    private Integer age;
    @ExcelProperty(value = "班级")
    private String clazz;
}

```



#### excel

![image-20220621170650838](https://blog-1252734679.cos.ap-shanghai.myqcloud.com/markdown/image-20220621170650838.png)



#### 实现代码

```java
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
```



### 8.读取为map

#### excel

![image-20220621170650838](https://blog-1252734679.cos.ap-shanghai.myqcloud.com/markdown/image-20220621170650838.png)



#### 监听器

```java
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

```



#### 实现代码

```java
@Test
//读取数据为map
public void readToMap() throws IOException {
  ClassPathResource excel = new ClassPathResource("excel/read/simpleRead.xlsx");
  //MapReadListener继承AnalysisEventListener
  //重写的 invoke() 方法中的 Map<Integer, Object> data，是列坐标 及该行该列的数据，可以根据业务在方法内再次组装
  EasyExcel.read(excel.getFile(), new MapReadListener()).sheet().doRead();
}
```


