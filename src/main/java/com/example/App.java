package com.example;

import com.example.config.AppConfig;
import com.example.config.DatabaseMigration;
import jakarta.servlet.MultipartConfigElement;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class App {
    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        Context context = tomcat.addContext("", null);

        AnnotationConfigWebApplicationContext springContext =
                new AnnotationConfigWebApplicationContext();

        springContext.register(AppConfig.class);
        springContext.setServletContext(context.getServletContext());
        springContext.refresh();

        DatabaseMigration migration =
                springContext.getBean(DatabaseMigration.class);

        migration.migrate();

        DispatcherServlet dispatcherServlet =
                new DispatcherServlet(springContext);

        Wrapper wrapper = Tomcat.addServlet(
                context,
                "dispatcher",
                dispatcherServlet
        );

        wrapper.setLoadOnStartup(1);
        context.addServletMappingDecoded("/", "dispatcher");

        wrapper.setMultipartConfigElement(new MultipartConfigElement(
                System.getProperty("java.io.tmpdir"),
                10_000_000,
                20_000_000,
                1_000_000
        ));

        tomcat.start();
        tomcat.getServer().await();
    }
}