import java.util.ArrayList;
import java.util.List;

public class Scenario {

    List<String> steps = new ArrayList<>();

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public void addStep(String step) {
        steps.add(step);
    }

    @Override
    public String toString() {
        return "Scenario{" +
                "steps=" + steps +
                '}';
    }
}
