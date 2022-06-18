package top.coolbreeze4j.easyexcellearn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EasyExcelLearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyExcelLearnApplication.class, args);
    }

}
