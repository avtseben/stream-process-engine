package ru.trueengineering.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Flow {

    List<Step> steps = new ArrayList<>();

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public Flow addStep(Step step) {
        steps.add(step);
        return this;
    }

    public String buildKey() {
        return steps.stream()
                .map(Step::getSemantic)
                .collect(Collectors.joining("_"));
    }

    @Override
    public String toString() {
        String hash = super.toString();
        return hash + "{" +
                "steps=" + steps +
                '}';
    }
}
