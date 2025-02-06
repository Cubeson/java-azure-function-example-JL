package org.example.functions.db;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {
    private static final EntityManagerFactory emf = null;
    private static EntityManagerFactory buildEntityManagerFactory(){
        Map<String, String> properties = new HashMap<>();
        String dbUrl = System.getenv("DB_CONNECTION_STRING");

        if (dbUrl == null || dbUrl.isEmpty()) {
            throw new RuntimeException("DATABASE_URL is not set!");
        }

        properties.put("jakarta.persistence.jdbc.url", dbUrl);
        properties.put("jakarta.persistence.jdbc.driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");

        return Persistence.createEntityManagerFactory("my-persistence-unit", properties);
    }
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void shutdown() {
        emf.close();
    }
}
