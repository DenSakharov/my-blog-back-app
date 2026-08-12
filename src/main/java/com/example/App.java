package com.example;

import com.example.config.AppConfig;
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

        DispatcherServlet dispatcherServlet =
                new DispatcherServlet(springContext);

        Wrapper wrapper = Tomcat.addServlet(
                context,
                "dispatcher",
                dispatcherServlet
        );

        wrapper.setLoadOnStartup(1);
        context.addServletMappingDecoded("/", "dispatcher");

        tomcat.start();
        tomcat.getServer().await();
    }
}