package ru.trueengineering.model;

import ru.trueengineering.model.LogRow;

public class Action {

    private LogRow logRow;
    private String semantic;

    public Action(LogRow logRow, String semantic) {
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
        return "ru.trueengineering.model.Action{" +
//                "logRow=" + logRow +
                "semantic='" + semantic + '\'' +
                '}';
    }

    public void setSemantic(String semantic) {
        this.semantic = semantic;
    }
}
