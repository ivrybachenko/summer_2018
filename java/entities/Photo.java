package app.entities;

public class Photo {
    private String url;
    private String sDescr;
    private String lDescr;

    public Photo(String url, String sDescr, String lDescr) {
        this.url = url;
        this.sDescr = sDescr;
        this.lDescr = lDescr;
    }

    public String getUrl() {
        return url;

    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getsDescr() {
        return sDescr;
    }

    public void setsDescr(String sDescr) {
        this.sDescr = sDescr;
    }

    public String getlDescr() {
        return lDescr;
    }

    public void setlDescr(String lDescr) {
        this.lDescr = lDescr;
    }
}
