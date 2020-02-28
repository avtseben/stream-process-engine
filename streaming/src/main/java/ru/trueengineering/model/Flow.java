package ru.trueengineering.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Flow {

    List<Action> actions = new ArrayList<>();

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public Flow addAction(Action action) {
        actions.add(action);
        return this;
    }

    public String buildKey() {
        return actions.stream()
                .map(Action::getSemantic)
                .collect(Collectors.joining("_"));
    }

    @Override
    public String toString() {
        String hash = super.toString();
        return hash + "{" +
                "actions=" + actions +
                '}';
    }
}
