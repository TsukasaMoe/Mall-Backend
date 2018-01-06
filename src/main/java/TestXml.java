import cn.tsukasalwq.utils.PropertiesUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MethodReplacer;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class TestXml {

    public static void main(String[] args) {
        // 在classpath的相对路径
//        ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext-datasource.xml");

        // 项目根目录的相对路径
//        FileSystemXmlApplicationContext context1 = new FileSystemXmlApplicationContext("src/main/resources/applicationContext-datasource.xml");

        // classpath下的文件
//        FileSystemXmlApplicationContext context2 = new FileSystemXmlApplicationContext("classpath:applicationContext-datasource.xml");

        // 绝对路径
//        FileSystemXmlApplicationContext context3 = new FileSystemXmlApplicationContext("file:D:\\Workplace\\IntellijIdeaProject\\mmall\\src\\main\\resources\\applicationContext-datasource.xml");
//        System.out.println(context3.getDisplayName());

        // 读取某个properties文件
//        String fileName = "mmall.properties";
//        Properties props = new Properties();
//        try {
//            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(props.getProperty("password.salt"));

        // 在classpath的相对路径
        BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("/applicationContext-datasource.xml"));
        System.out.println(beanFactory);

        System.out.println(new DateTime().plusDays(1));


    }
}
