package org.example.functions.db;

import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.cfg.Configuration;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");

            // Pobranie connection stringa z ENV
            String dbUrl = System.getenv("DB_CONNECTION_STRING");

            if (dbUrl == null || dbUrl.isEmpty()) {
                throw new RuntimeException("DB_CONNECTION_STRING is not set!");
            }

            configuration.setProperty("hibernate.connection.url", dbUrl);
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
            configuration.setProperty("hibernate.connection.driver_class", "com.microsoft.sqlserver.jdbc.SQLServerDriver");

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

            return new MetadataSources(standardRegistry)
                .buildMetadata()
                .buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}