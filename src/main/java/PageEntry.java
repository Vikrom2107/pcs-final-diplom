public class PageEntry implements Comparable<PageEntry> {
    private final String pdfName;
    private final int page;
    private int count;

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    public String getPdfName() {
        return pdfName;
    }

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }
    public String toString() {
        return pdfName + " " + page + " " + count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int compareTo(PageEntry o) {
        if (getCount() > o.getCount())
            return -1;
        if (getCount() < o.getCount())
            return 1;
        return 0;
    }

}
