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
    public Task getSPT(ArrayList<Task> remaining_tasks, Instance instance) {
        assert remaining_tasks != null && !remaining_tasks.isEmpty();

        int min = Integer.MAX_VALUE;
        int time_min = Integer.MAX_VALUE;
        Task spt_task = null;
        ArrayList<Task> best_tasks = new ArrayList<>();

        for (Task t : remaining_tasks) {
            System.out.println(t + " : " + instance.duration(t));

            if (t.is_possible) {

                // MODE EST_SPT
                if (this.priority == Priority.EST_SPT) {
                    int t_start_time = t.start_time;

                    if (t_start_time < time_min) {
                        best_tasks.clear();
                        best_tasks.add(t);
                        time_min = t_start_time;
                    }
                    else if (t_start_time == time_min) {
                        best_tasks.add(t);
                    }
                }
                // MODE SPT
                else if (this.priority == Priority.SPT) {
                    best_tasks.add(t);
                }
            }
        }

        for (Task t : best_tasks) {
            if (instance.duration(t) < min) {
                min = instance.duration(t);
                spt_task = t;
            }
        }

        return spt_task;
    }

    public Task getLRPT(ArrayList<Task> remaining_tasks, Instance instance) {
        assert remaining_tasks != null && !remaining_tasks.isEmpty();
        int aux_duration=0;
        int max = Integer.MIN_VALUE;
        Task lrpt_task = null;
        for (Task t : remaining_tasks) {
            System.out.println(t + " : " + instance.duration(t));
            for (int j = t.task; j < instance.numTasks; j++) {
                aux_duration += instance.duration(t.job, j);
            }

            if (t.is_possible && aux_duration > max) {
                max = aux_duration;
                lrpt_task = t;
            }
            aux_duration = 0;
        }
        return lrpt_task;
    }

    public Task getEST_LRPT(ArrayList<Task> remaining_tasks, Instance instance) {
        assert remaining_tasks != null && !remaining_tasks.isEmpty();
        int aux_duration = 0;
        int max = Integer.MIN_VALUE;

        int time_min = Integer.MAX_VALUE;
        ArrayList<Task> best_tasks = new ArrayList<>();

        Task lrpt_task = null;
        for (Task t : remaining_tasks) {
            System.out.println(t + " : " + instance.duration(t));


            if (t.is_possible) {

                int t_start_time = t.start_time;

                if (t_start_time < time_min) {
                    best_tasks.clear();
                    best_tasks.add(t);
                    time_min = t_start_time;
                }
                else if (t_start_time == time_min) {
                    best_tasks.add(t);
                }
            }
        }

        for (Task t : best_tasks) {

            // Calculate remaining time for the task
            for (int j = t.task; j < instance.numTasks; j++) {
                aux_duration += instance.duration(t.job, j);
            }

            // Update the max if needed
            if (aux_duration > max) {
                max = aux_duration;
                lrpt_task = t;
            }

            aux_duration = 0;
        }

        return lrpt_task;
    }

    @Override
    public Optional<Schedule> solve(Instance instance, long deadline) {

        System.out.println("START");

        // resource order that will be populated (initially empty)
        ResourceOrder sol = new ResourceOrder(instance);
        
        // Create array of tasks to add to the RO
        // Only the first tasks are deemed possible
        ArrayList<Task> remaining_tasks = instance.getAllTasks();

        while (!remaining_tasks.isEmpty()) {
            Task highest_prio = null;

            if (this.priority == Priority.SPT || this.priority == Priority.EST_SPT) {
                highest_prio = this.getSPT(remaining_tasks, instance);
            }
            else if (this.priority == Priority.LRPT) {
                highest_prio = this.getLRPT(remaining_tasks, instance);
            }
            else if (this.priority == Priority.EST_LRPT) {
                highest_prio = this.getEST_LRPT(remaining_tasks, instance);
            }

            System.out.println("Selected task : "+highest_prio);
            assert highest_prio != null;
            int machine = instance.machine(highest_prio);
            sol.addTaskToMachine(machine, highest_prio);

            // Remove task that was just added to the schedule
            remaining_tasks.remove(highest_prio);

            // Update which tasks are possible
            if (highest_prio.next_task != null) {
                highest_prio.next_task.is_possible = true;
            }
        }

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
