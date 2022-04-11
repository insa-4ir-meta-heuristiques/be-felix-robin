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

    public Schedule getBestMakespan(List<ResourceOrder> neighbours) {
        return null;
    }

    @Override
    public Optional<Schedule> solve(Instance instance, long deadline) {
        /*
        repeat
        {Sélectionner le meilleur voisin s′ sur tout le voisinage de s}
        s′ ← (s′ ∈ N eighboor(s) tq ∀s′′ ∈ N eighboor(s), M akespan(s′) ≤ M akespan(s′′))
        if M akespan(s′) < M akespan(s) then
        s ← s′
        end if
        until pas de voisin améliorant ou time out
         */

        Optional<Schedule> os = new GreedySolver(GreedySolver.Priority.EST_SPT).solve(instance, deadline);
        Schedule s;

        if (os.isPresent()) {
            s = os.get();
        }

        else {
            throw new UnsupportedOperationException();
        }

        int makespan = Integer.MAX_VALUE;
        boolean changed = true;
        List<ResourceOrder> neighbours = this.neighborhood.generateNeighbors(new ResourceOrder(s));

        while (changed) {

            changed = false;

            for (ResourceOrder r : neighbours) {
                if (r.toSchedule().isPresent()) {

                    int new_makespan = r.toSchedule().get().makespan();

                    // trouver le minimum parmis les voisins (getBestMakespan ?)
                    // quand on a trouvé le min on compare le makespan

                    if (new_makespan < makespan) {
                        changed = true;
                        makespan = new_makespan;
                    }
                }
                ;
            }

        }
        return null;

    }

}
