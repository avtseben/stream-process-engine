package ru.trueengineering.model;

import ru.trueengineering.model.LogRow;

public class Step {

    private LogRow logRow;
    private String semantic;

    public Step(LogRow logRow, String semantic) {
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

    @Override
    public String toString() {
        return "ru.trueengineering.model.Step{" +
//                "logRow=" + logRow +
                "semantic='" + semantic + '\'' +
                '}';
    }

    public void setSemantic(String semantic) {
        this.semantic = semantic;
    }
}
