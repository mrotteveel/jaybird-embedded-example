package nl.lawinegevaar.jaybird.embedded;

import org.apache.commons.cli.*;

public class AppConfig {

    private boolean createDatabase = false;
    private String databasePath = "embedded-example.fdb";
    private String user = "sysdba";
    private String charset = "utf-8";
    private String cachedUrl;

    public boolean isCreateDatabase() {
        return createDatabase;
    }

    public void setCreateDatabase(boolean createDatabase) {
        this.createDatabase = createDatabase;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        cachedUrl = null;
        this.databasePath = databasePath;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        cachedUrl = null;
        this.charset = charset;
    }

    public String getJdbUrl() {
        String url = cachedUrl;
        if (url == null) {
            url = cachedUrl = "jdbc:firebirdsql:embedded:" + getDatabasePath() + "?charSet=" + getCharset();
        }
        return url;
    }

    /**
     * Parse the command line arguments into an {@code AppConfig} object.
     *
     * @param args Command line arguments
     * @return AppConfig, or {@code null} if {@code --help} was requested or if there was an exception on parsing
     */
    public static AppConfig parseCommandLine(String[] args) {
        Options options = getCommandLineOptions();
        CommandLineParser commandLineParser = new DefaultParser();

        try {
            CommandLine commandLine = commandLineParser.parse(options, args);
            if (commandLine.hasOption("help")) {
                printUsage(options);
                return null;
            }

            AppConfig appConfig = new AppConfig();

            if (commandLine.hasOption("create")) {
                appConfig.setCreateDatabase(true);
            }

            if (commandLine.hasOption("database")) {
                appConfig.setDatabasePath(commandLine.getOptionValue("database"));
            }

            if (commandLine.hasOption("user")) {
                appConfig.setUser(commandLine.getOptionValue("user"));
            }

            if (commandLine.hasOption("charset")) {
                appConfig.setCharset(commandLine.getOptionValue("charset"));
            }

            return appConfig;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            printUsage(options);
            return null;
        }
    }

    public static Options getCommandLineOptions() {
        Options options = new Options();
        options.addOption(Option.builder().longOpt("help").desc("Print usage").build());
        options.addOption(Option.builder().longOpt("create").desc("Create database").build());
        options.addOption(Option.builder().longOpt("database").hasArg(true).argName("DB_PATH").desc("Database path").build());
        options.addOption(Option.builder().longOpt("user").hasArg(true).argName("USER").desc("User").build());
        options.addOption(Option.builder().longOpt("charset").hasArg(true).argName("CHARSET").desc("Connection character set").build());
        return options;
    }

    private static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("embedded-example", options, true);
    }

}
