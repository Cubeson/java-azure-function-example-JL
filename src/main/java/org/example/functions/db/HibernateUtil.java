package org.example.functions.db;

import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    @Getter
    private static SessionFactory sessionFactory = null;

    static {
        Configuration cfg = new Configuration().configure();
        String dbUrl = System.getenv("DB_CONNECTION_STRING");
        if (dbUrl == null || dbUrl.isEmpty()) {
            throw new RuntimeException("DB_CONNECTION_STRING is not set!");
        }
        cfg.setProperty("hibernate.connection.url", dbUrl);
        cfg.addAnnotatedClass(Person.class);
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                .applySettings(cfg.getProperties());
        sessionFactory = cfg.buildSessionFactory(builder.build());
    }

    /*private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            String dbUrl = System.getenv("DB_CONNECTION_STRING");
            if (dbUrl == null || dbUrl.isEmpty()) {
                throw new RuntimeException("DB_CONNECTION_STRING is not set!");
            }

            configuration.setProperty("hibernate.connection.url", dbUrl);
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(builder.build());



            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

            return new MetadataSources(standardRegistry)
                .buildMetadata()
                .buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }*/
}