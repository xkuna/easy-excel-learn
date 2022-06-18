package top.coolbreeze4j.easyexcellearn.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CoolBreeze
 * @date 2022/6/18 20:05.
 */
public class GenerateData {
    public static List<DemoData> data() {
        List<DemoData> list = new ArrayList<>();
        for (int i = 1; i <=10 ; i++) {
            DemoData demoData = new DemoData();
            demoData.setName("张" + i);
            demoData.setAge(20+i);
            demoData.setClazz("建筑工程学院-土木工程" + i + "班");
            list.add(demoData);
        }
        return list;
    }
}
