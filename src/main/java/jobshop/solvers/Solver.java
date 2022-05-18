package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.Schedule;
import jobshop.solvers.neighborhood.Neighborhood;
import jobshop.solvers.neighborhood.Nowicki;

import java.util.ArrayList;
import java.util.Optional;

/** Common interface that must implemented by all solvers. */
public interface Solver {

    /** Look for a solution until blocked or a deadline has been met.
     *
     * @param instance Jobshop instance that should be solved.
     * @param deadline Absolute time at which the solver should have returned a solution.
     *                 This time is in milliseconds and can be compared with System.currentTimeMilliseconds()
     * @return An optional schedule that will be non empty if a solution was found.
     */
    Optional<Schedule> solve(Instance instance, long deadline, int maxIter);



    /** Static factory method to create a new solver based on its name. */
    static Solver getSolver(String name) {

        Nowicki no = new Nowicki();
        boolean random = false;

        int n_iter = 20;

        switch (name) {
            case "basic": return new BasicSolver();
            case "spt": return new GreedySolver(GreedySolver.Priority.SPT, random, n_iter);
            case "lrpt": return new GreedySolver(GreedySolver.Priority.LRPT, random, n_iter);
            case "est_lrpt": return new GreedySolver(GreedySolver.Priority.EST_LRPT, random, n_iter);
            case "est_spt": return new GreedySolver(GreedySolver.Priority.EST_SPT, random, n_iter);
            case "taboo": return new TabooSolver(no, new GreedySolver(GreedySolver.Priority.EST_SPT, random, n_iter), 5);
            case "descent_lrpt": return new DescentSolver(no, new GreedySolver(GreedySolver.Priority.LRPT, random, n_iter));
            default: throw new RuntimeException("Unknown solver: "+ name);
        }
    }
}


