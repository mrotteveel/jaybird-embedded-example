package nl.lawinegevaar.jaybird.embedded;

import org.firebirdsql.gds.impl.jni.EmbeddedGDSFactoryPlugin;
import org.firebirdsql.management.FBManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class Main {

    public static void main(String[] args) throws Exception {
        AppConfig appConfig = AppConfig.parseCommandLine(args);
        if (appConfig == null) {
            System.exit(-1);
        }
        tryConfigureJNA();
        if (appConfig.isCreateDatabase()) {
            tryCreateDatabase(appConfig);
        }

        try (Connection connection = DriverManager
                .getConnection(appConfig.getJdbUrl(), appConfig.getUser(), "")) {
            DatabaseMetaData md = connection.getMetaData();
            System.out.println("database product version: "
                    + md.getDatabaseProductVersion());

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "select mon$database_name from mon$database")) {
                if (rs.next()) {
                    System.out.println("Database name: " + rs.getString(1));
                }
            }
        }
    }

    private static void tryConfigureJNA() {
        String jnaPath = System.getProperty("jna.library.path");
        if (jnaPath == null || jnaPath.isEmpty()) {
            Path path = Paths.get("fb").toAbsolutePath();
            System.out.println("Attempting to set jna.library.path to: " + path);
            System.setProperty("jna.library.path", path.toString());
        }
    }

    private static void tryCreateDatabase(AppConfig appConfig) throws Exception {
        FBManager fbManager = new FBManager(EmbeddedGDSFactoryPlugin.EMBEDDED_TYPE_NAME);
        fbManager.start();
        try {
            fbManager.createDatabase(appConfig.getDatabasePath(), appConfig.getUser(), "");
        } finally {
            fbManager.stop();
        }
    }
}
