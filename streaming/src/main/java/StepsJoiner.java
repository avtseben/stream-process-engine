import org.apache.kafka.streams.kstream.ValueJoiner;

/**
 * здесь join просто поиграться нет никакого смысла
 */
public class StepsJoiner implements ValueJoiner<Wrapper, Wrapper, Scenario> {

    @Override
    public Scenario apply(Wrapper step1, Wrapper step2) {
        Scenario scenario = new Scenario();
        scenario.addStep(step1.getSemantic());
        scenario.addStep(step2.getSemantic());
        return scenario;
    }
}
