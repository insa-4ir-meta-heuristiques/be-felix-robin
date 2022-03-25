package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.encodings.Task;

import java.util.Optional;

/** An empty shell to implement a greedy solver. */
public class GreedySolver implements Solver {

    /** All possible priorities for the greedy solver. */
    public enum Priority {
        SPT, LPT, SRPT, LRPT, EST_SPT, EST_LPT, EST_SRPT, EST_LRPT
    }

    /** Priority that the solver should use. */
    final Priority priority;

    /** Creates a new greedy solver that will use the given priority. */
    public GreedySolver(Priority p) {

        this.priority = p;

    }

    @Override
    public Optional<Schedule> solve(Instance instance, long deadline) {

        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        // resource order that will be populated (initially empty)
        ResourceOrder sol = new ResourceOrder(instance);

        Task t = new Task(1, 1);

        sol.addTaskToMachine(0, new Task(0, 0));
        sol.addTaskToMachine(1, new Task(1, 0));
        sol.addTaskToMachine(1, new Task(0, 1));
        sol.addTaskToMachine(0, t);
        sol.addTaskToMachine(2, new Task(0, 2));
        sol.addTaskToMachine(2, new Task(1, 2));

        System.out.println(t.getPredecessorsInResourceOrder(sol));

        throw new UnsupportedOperationException();
    }
}
