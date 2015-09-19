package model;




import java.io.File;

import common.parser.ServerParser;

public class FileItem {
    private File file;
    private ServerParser parser;

    public FileItem(final File file) {
        this.file = file;
        parser = new ServerParser(file);
    }

    public String getName() {
        return file.getName();
    }

    public String getPath() {
        return file.getPath();
    }

    public String getAddress() {
        return parser.getAddress();
    }

    public String getType() {
        if (parser.isLighttpd()) {
            return "lighttpd";
        } else if (parser.isNginx()) {
            return "Nginx";
        }
        return "";
    }

    public File getFile() {
        return file;
    }

    public ServerParser getParser() {
        return parser;
    }
}
