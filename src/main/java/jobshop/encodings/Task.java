package jobshop.encodings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/** Represents a task (job,task) of a jobshop problem.
 *
 * Example : (2, 3) represents the fourth task of the third job. (remember that we start counting at 0)
 **/
public final class Task {

    /** Identifier of the job */
    public final int job;

    /** Start time of the task */
    public int start_time;

    /** Index of the task inside the job. */
    public final int task;

    /** If the task can be executed next. */
    public boolean is_possible;

    /** Which Task is next. */
    public Task next_task;

    /** Creates a new Task object (job, task). */
    public Task(int job, int task) {
        this.job = job;
        this.task = task;
        this.is_possible = false;
        this.next_task = null;
    }

    public Task(int job, int task, Task next_task) {
        this.job = job;
        this.task = task;
        this.is_possible = false;
        this.next_task = next_task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task1 = (Task) o;
        return job == task1.job &&
                task == task1.task;
    }

    public boolean containsTaskWithIndex(Collection<? extends Task> collection, int task) {
        return collection.stream().anyMatch(t -> t.task == task);
    }

    public boolean isPossible(ResourceOrder resourceOrder) {

        // The predecessors actually present in the RO
        ArrayList<Task> pred_tasks_ro = this.getOtherTasksInResourceOrder(resourceOrder);

        // A task cannot be present twice in the RO
        if (pred_tasks_ro.contains(this)) {return false;}

        // Contains all predecessors needed, we remove them when we find them
        ArrayList<Integer> pred_tasks_necessary = this.getNecessaryPredecessors();

        for (int t : pred_tasks_necessary) {
            if (!this.containsTaskWithIndex(pred_tasks_ro, t)) {
                return false;
            }
        }

        return true;
    }

    public ArrayList<Integer> getNecessaryPredecessors() {
        ArrayList<Integer> necessary_pred = new ArrayList<>();
        for (int i = 0; i < this.task; i++) {
            necessary_pred.add(i);
        }
        return necessary_pred;
    }

    public ArrayList<Task> getOtherTasksInResourceOrder(ResourceOrder resourceOrder) {


        Task[][] tasks = resourceOrder.tasksByMachine;
        ArrayList<Task> predecessors = new ArrayList<>();

        // Iterate through the machines with i
        for (int i = 0; i < tasks.length; i++) {
            // Iterate through the jobs with j
            for (int j = 0; j < tasks[i].length; j++) {
                // If the spot is not empty and has the right job number
                if (tasks[i][j] != null && tasks[i][j].job == this.job) {
                    Task t = tasks[i][j];
                    predecessors.add(t);
                }

            }

        }
        return predecessors;
    }

    @Override
    public int hashCode() {
        return Objects.hash(job, task);
    }

    @Override
    public String toString() {
        return "(" + job +", " + task + ')';
    }
}
