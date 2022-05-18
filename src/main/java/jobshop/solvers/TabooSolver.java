package jobshop.solvers;


import jobshop.Instance;
import jobshop.encodings.Changes;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.solvers.neighborhood.Neighborhood;
import jobshop.solvers.neighborhood.Nowicki;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TabooSolver implements Solver {

    final Nowicki neighborhood;
    final Solver baseSolver;
    final int dureeTaboo;

    /** Creates a new descent solver with a given neighborhood and a solver for the initial solution.
     *  @param neighborhood Neighborhood object that should be used to generates neighbor solutions to the current candidate.
     * @param baseSolver A solver to provide the initial solution.
     * @param tabooSize
     */
    public TabooSolver(Neighborhood neighborhood, Solver baseSolver, int tabooSize) {
        this.neighborhood = (Nowicki) neighborhood;
        this.baseSolver = baseSolver;
        this.dureeTaboo = tabooSize;
    }

    @Override
    public Optional<Schedule> solve(Instance instance, long deadline, int maxIter) {

        Optional<Schedule> os = this.baseSolver.solve(instance, deadline, maxIter);
        Schedule s;
        Schedule s_mem;
        ResourceOrder sol=null;
        ResourceOrder r=null;
        if (os.isPresent()) {
            s = os.get();
        } else {
            throw new UnsupportedOperationException();
        }
        s_mem=s;
        List<Changes> Taboo =new ArrayList<>();
        int compteur=0;
        int makespan = Integer.MAX_VALUE;
        ResourceOrder ro=null;
        List<Nowicki.Swap> swaps = null;
        Changes change_final =null;
        int index=0;

        while (compteur<maxIter && System.currentTimeMillis()<deadline) {
            compteur+=1;
            ro=new ResourceOrder(s);
            swaps = this.neighborhood.allSwaps(ro);
            for (Nowicki.Swap swap : swaps) {
                Changes change = new Changes(ro.getTaskOfMachine(swap.machine, swap.t1), ro.getTaskOfMachine(swap.machine, swap.t2));
                r = swap.generateFrom(ro);
                if (r.toSchedule().isPresent()) {
                    if (!Taboo.contains(change)) {
                        int new_makespan = r.toSchedule().get().makespan();
                        if (new_makespan < makespan) {
                            makespan = new_makespan;
                            sol = r;
                            change_final = change;
                        }
                    } else {
                        int new_makespan = r.toSchedule().get().makespan();
                        if (new_makespan < s_mem.makespan()) {
                            makespan = new_makespan;
                            sol = r;
                            change_final = change;
                        }
                    }
                }
            }
            Taboo.add(index, change_final);
            index=(index+1) % this.dureeTaboo;
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