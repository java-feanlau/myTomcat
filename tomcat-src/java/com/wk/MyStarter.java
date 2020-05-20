package com.wk;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

public class MyStarter {
    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();
        tomcat.setHostname("localhost");
        tomcat.setPort(9090);
        Context context = tomcat.addContext("/embed", null);
        HttpServlet httpServlet = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.getWriter().write("hello, this is tomcat source.");
            }

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                doGet(req, resp);
            }
        };

        Filter filter = new Filter() {

            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
                System.out.println("filter init");
            }

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                System.out.println("filter do filter");
                chain.doFilter(request, response);
            }

            @Override
            public void destroy() {
                System.out.println("destory");
            }
        };
        tomcat.addServlet(context, "dispatch", httpServlet);
        context.addServletMappingDecoded("/dis", "dispatch");

        FilterDef filterDef = new FilterDef();
        filterDef.setFilter(filter);
        filterDef.setFilterName("myFilter");
        filterDef.addInitParameter("username", "Allen");

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("myFilter");
        filterMap.setCharset(Charset.forName("UTF-8"));
        filterMap.addURLPatternDecoded("/");
        filterMap.addServletName("*");

        context.addFilterDef(filterDef);
        context.addFilterMap(filterMap);

        try{
            tomcat.init();
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }

    }
}
