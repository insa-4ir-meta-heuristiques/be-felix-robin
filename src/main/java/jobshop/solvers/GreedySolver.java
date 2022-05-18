package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.encodings.Task;
import jobshop.solvers.neighborhood.Nowicki;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.lang.Math;

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

        int time_min = Integer.MAX_VALUE;
        ArrayList<Task> best_tasks = new ArrayList<>();

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

        return getLRPT(best_tasks, instance);
    }


    public Optional<Schedule> solve(Instance instance, long deadline, int maxIter) {
        int index;
        System.out.println("START");

        // resource order that will be populated (initially empty)
        ResourceOrder sol = new ResourceOrder(instance);

        // Create array of tasks to add to the RO
        // Only the first tasks are deemed possible
        ArrayList<Task> remaining_tasks = instance.getAllTasks();
        ArrayList<Task> possible_tasks = new ArrayList<>();

        while (!remaining_tasks.isEmpty()) {
            index = (int)(Math.random() * 100);
            Task highest_prio = null;
            if (index<=95) {
                if (this.priority == Priority.SPT || this.priority == Priority.EST_SPT) {
                    highest_prio = this.getSPT(remaining_tasks, instance);
                } else if (this.priority == Priority.LRPT) {
                    highest_prio = this.getLRPT(remaining_tasks, instance);
                } else if (this.priority == Priority.EST_LRPT) {
                    highest_prio = this.getEST_LRPT(remaining_tasks, instance);
                }
            }
            else{
                for(Task t: remaining_tasks) {
                    if (t.is_possible){
                        possible_tasks.add(t);
                    }
                }
                index = (int)(Math.random() * possible_tasks.size());
                highest_prio=possible_tasks.get(index);
            }
            System.out.println("Selected task : "+highest_prio);
            assert highest_prio != null;
            int machine = instance.machine(highest_prio);
            sol.addTaskToMachine(machine, highest_prio);

            // Remove task that was just added to the schedule
            remaining_tasks.remove(highest_prio);
            possible_tasks.remove(highest_prio);

            // Update which tasks are possible
            if (highest_prio.next_task != null) {
                highest_prio.next_task.is_possible = true;
            }
        }
        System.out.println("////////////////////");
        System.out.println(sol);
        Nowicki n = new Nowicki();
        List<Nowicki.Block> blocks = n.blocksOfCriticalPath(sol);
        System.out.println(blocks);
        System.out.println(n.neighbors(blocks.get(0)));


        return sol.toSchedule();
    }
}