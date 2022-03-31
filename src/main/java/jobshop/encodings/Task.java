package jobshop.encodings;

import java.util.ArrayList;
import java.util.Objects;

/** Represents a task (job,task) of a jobshop problem.
 *
 * Example : (2, 3) represents the fourth task of the third job. (remember that we start counting at 0)
 **/
public final class Task {

    /** Identifier of the job */
    public final int job;

    /** Index of the task inside the job. */
    public final int task;

    /** Creates a new Task object (job, task). */
    public Task(int job, int task) {
        this.job = job;
        this.task = task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task1 = (Task) o;
        return job == task1.job &&
                task == task1.task;
    }


    public boolean isPossible(ResourceOrder resourceOrder) {

        // The predecessors actually present in the RO
        ArrayList<Task> pred_tasks_ro = this.getOtherTasksInResourceOrder(resourceOrder);

        // A task cannot be present twice in the RO
        if (pred_tasks_ro.contains(this)) {return false;}

        // Contains all predecessors needed, we remove them when we find them
        ArrayList<Task> pred_tasks_necessary = this.getNecessaryPredecessors();

        for (int i = 0; i < pred_tasks_necessary.size(); i++) {
            Task t = pred_tasks_necessary.get(i);
            if (!pred_tasks_ro.contains(t)) {
                return false;
            }
        }

        return true;
    }

    public ArrayList<Task> getNecessaryPredecessors() {
        ArrayList<Task> necessary_pred = new ArrayList<>();
        for (int i = 0; i < this.task; i++) {
            Task pred = new Task(this.job, i);
            necessary_pred.add(pred);
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
