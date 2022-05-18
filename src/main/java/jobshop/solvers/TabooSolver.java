package jobshop.solvers;


import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.solvers.neighborhood.Neighborhood;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public class TabooSolver implements Solver {

    final Neighborhood neighborhood;
    final Solver baseSolver;

    /** Creates a new descent solver with a given neighborhood and a solver for the initial solution.
     *
     * @param neighborhood Neighborhood object that should be used to generates neighbor solutions to the current candidate.
     * @param baseSolver A solver to provide the initial solution.
     */
    public TabooSolver(Neighborhood neighborhood, Solver baseSolver) {
        this.neighborhood = neighborhood;
        this.baseSolver = baseSolver;
    }

    @Override
    public Optional<Schedule> solve(Instance instance, long deadline, int maxIter) {

        Optional<Schedule> os = this.baseSolver.solve(instance, deadline, maxIter);
        Schedule s;
        Schedule s_mem;
        ResourceOrder sol=null;
        if (os.isPresent()) {
            s = os.get();
        } else {
            throw new UnsupportedOperationException();
        }
        s_mem=s;
        int compteur=0;
        int makespan = Integer.MAX_VALUE;
        List<ResourceOrder> neighbours = this.neighborhood.generateNeighbors(new ResourceOrder(s));

        while (compteur<maxIter && System.currentTimeMillis()<deadline) {
            compteur+=1;
            neighbours = this.neighborhood.generateNeighbors(new ResourceOrder(s));
            for (ResourceOrder r : neighbours) {
                if (r.toSchedule().isPresent()) {
                    int new_makespan = r.toSchedule().get().makespan();
                    if (new_makespan < makespan) {
                        makespan = new_makespan;
                        sol=r;
                    }
                }
            }
            if (makespan< s_mem.makespan()){
                s_mem=sol.toSchedule().get();
            }
            if  (sol.toSchedule().isPresent()) {
                s = sol.toSchedule().get();
            }
        }
        return Optional.of(s_mem);
    }

}