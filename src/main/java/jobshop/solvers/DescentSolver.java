package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.solvers.neighborhood.Neighborhood;

import java.util.List;
import java.util.Optional;

/** An empty shell to implement a descent solver. */
public class DescentSolver implements Solver {

    final Neighborhood neighborhood;
    final Solver baseSolver;

    /** Creates a new descent solver with a given neighborhood and a solver for the initial solution.
     *
     * @param neighborhood Neighborhood object that should be used to generates neighbor solutions to the current candidate.
     * @param baseSolver A solver to provide the initial solution.
     */
    public DescentSolver(Neighborhood neighborhood, Solver baseSolver) {
        this.neighborhood = neighborhood;
        this.baseSolver = baseSolver;
    }

    @Override
    public Optional<Schedule> solve(Instance instance, long deadline, int maxIter) {


        Optional<Schedule> os = this.baseSolver.solve(instance, deadline, maxIter);
        Schedule s;
        ResourceOrder sol = null;
        if (os.isPresent()) {
            s = os.get();
        }

        else {
            throw new UnsupportedOperationException();
        }

        int makespan = Integer.MAX_VALUE;
        boolean changed = true;
        List<ResourceOrder> neighbours = this.neighborhood.generateNeighbors(new ResourceOrder(s));

        while (changed && System.currentTimeMillis()<deadline) {

            changed = false;
            //int min_makespan = Integer.MAX_VALUE;
            neighbours = this.neighborhood.generateNeighbors(new ResourceOrder(s));
            for (ResourceOrder r : neighbours) {
                if (r.toSchedule().isPresent()) {

                    int new_makespan = r.toSchedule().get().makespan();

                    if (new_makespan < makespan) {
                        makespan = new_makespan;
                        sol = r;
                    }
                }
            }
            if(makespan < s.makespan()){
                s = sol.toSchedule().get();
                changed = true;
            }
        }
        return Optional.of(s);

    }
}