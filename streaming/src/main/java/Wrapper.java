public class Wrapper {

    private LogRow logRow;
    private String semantic;

    public Wrapper(LogRow logRow, String semantic) {
        this.logRow = logRow;
        this.semantic = semantic;
    }

    public LogRow getLogRow() {
        return logRow;
    }

    public void setLogRow(LogRow logRow) {
        this.logRow = logRow;
    }

    public String getSemantic() {
        return semantic;
    }

    public void setSemantic(String semantic) {
        this.semantic = semantic;
    }
}
