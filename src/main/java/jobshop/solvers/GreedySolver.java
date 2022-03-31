package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.encodings.Task;

import java.lang.reflect.Array;
import java.util.ArrayList;
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

    // Fails if remaining_tasks is null or empty
    public Task getSPT(ArrayList<Task> remaining_tasks, ResourceOrder resourceOrder, Instance instance) {
        assert remaining_tasks != null && !remaining_tasks.isEmpty();

        int min = Integer.MAX_VALUE;
        Task spt_task = null;
        for (int i = 0; i < remaining_tasks.size(); i++) {
            Task t = remaining_tasks.get(i);
            System.out.println(t + " : " + instance.duration(t));
            if (t.isPossible(resourceOrder) && instance.duration(t) < min) {
                min = instance.duration(t);
                spt_task = t;
            }
        }
        return spt_task;
    }

    public Task getLRPT(ArrayList<Task> remaining_tasks, ResourceOrder resourceOrder, Instance instance) {
        assert remaining_tasks != null && !remaining_tasks.isEmpty();
        int aux_duration=0;
        int max = Integer.MIN_VALUE;
        Task lrpt_task = null;
        for (int i = 0; i < remaining_tasks.size(); i++) {
            Task t = remaining_tasks.get(i);
            System.out.println(t + " : " + instance.duration(t));
            for (int j = t.task; j < instance.numTasks; j++) {
                aux_duration+=instance.duration(t.job, j);
            }
            if (t.isPossible(resourceOrder) && aux_duration > max) {
                max = aux_duration;
                lrpt_task = t;
            }
            aux_duration=0;
        }
        return lrpt_task;
    }

    @Override
    public Optional<Schedule> solve(Instance instance, long deadline) {

        System.out.println("START");

        // resource order that will be populated (initially empty)
        ResourceOrder sol = new ResourceOrder(instance);
        
        // Create array of tasks to add to the RO
        ArrayList<Task> remaining_tasks = instance.getAllTasks();

        while (!remaining_tasks.isEmpty()) {
            Task highest_prio = null;
            if (this.priority == Priority.SPT) {
                highest_prio = this.getSPT(remaining_tasks, sol, instance);
            }
            else if (this.priority == Priority.LRPT) {
                highest_prio = this.getLRPT(remaining_tasks, sol, instance);

            }
            System.out.println("Selected task : "+highest_prio);
            assert highest_prio != null;
            int machine = instance.machine(highest_prio);
            sol.addTaskToMachine(machine, highest_prio);
            remaining_tasks.remove(highest_prio);
        }

        //throw new UnsupportedOperationException();
        return sol.toSchedule();
    }
}
/*
TESTS UNITAIRES isValid, getOtherTasksInResourceOrder, getNecessaryPredecessors
        Task t_0 = new Task(0, 0);
        sol.addTaskToMachine(0, t_0);
        Task t_val = new Task(0, 1);
        Task t_inval = new Task(0, 2);
        System.out.println("false : " + t_0.isPossible(sol));
        System.out.println("true : " + t_val.isPossible(sol));
        System.out.println("false : " + t_inval.isPossible(sol));

 */
