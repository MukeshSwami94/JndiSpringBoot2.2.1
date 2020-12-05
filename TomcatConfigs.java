package mt.config;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.NamingException;
import javax.sql.DataSource;

@Configuration
public class TomcatConfigs {

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                ContextResource resource = new ContextResource();
                resource.setType(DataSource.class.getName());
                resource.setName("j4s");
//                resource.setProperty("factory", "org.apache.tomcat.jdbc.pool.DataSourceFactory");
                resource.setProperty("driverClassName", "com.mysql.jdbc.Driver");
                resource.setProperty("url", "jdbc:mysql://localhost/demo");
                resource.setProperty("username", "root");
                resource.setProperty("password", "Mukesh@2015");
                context.getNamingResources().addResource(resource);
            }

            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                tomcat.enableNaming();
                TomcatWebServer container = super.getTomcatWebServer(tomcat);
                for (Container child : container.getTomcat().getHost().findChildren()) {
                    if (child instanceof Context) {
                        ClassLoader contextClassLoader = ((Context) child).getLoader().getClassLoader();
                        Thread.currentThread().setContextClassLoader(contextClassLoader);
                        break;
                    }
                }
                return container;
            }

        };
        return tomcat;


    }
    @Bean
    public DataSource jndiDataSource() throws IllegalArgumentException, NamingException
    {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName("java:/comp/env/j4s");
        bean.setProxyInterface(DataSource.class);
        bean.setLookupOnStartup(false);
        bean.afterPropertiesSet();

        return (DataSource) bean.getObject();
    }
}
