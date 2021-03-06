package model;

public class Repository {

    private String mName;
    private String mUrl;
    private String mCopyToPath;
    private long fileSize;

    public Repository(String mName, String mUrl, String mCopyToPath, long fileSize) {
        this.mName = mName;
        this.mUrl = mUrl;
        this.mCopyToPath = getCopyPath();
        this.fileSize = fileSize;
    }

    public String getName() {
        return mName;
    }

    public String getUrl() {
        return mUrl;
    }

    public long getSize() {
        return fileSize;
    }

    public String getCopyPath() {

        this.mCopyToPath = "\\/data\\/data\\/org.teachervirus";
        return mCopyToPath;
    }
}