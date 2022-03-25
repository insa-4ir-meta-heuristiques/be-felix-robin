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

    // TODO:
    public boolean isValid(ResourceOrder resourceOrder) {
        Task[][] tasks = resourceOrder.tasksByMachine;

        int nb_tasks_left = this.task;
        ArrayList<Task> pred_tasks_ro = this.getPredecessorsInResourceOrder(resourceOrder);
        //TODO : il faut obtenir les tâches prédécesseures total (pred_tasks_ro c'est les tâches pred dans le ro
        //TODO : après faut voir si toutes les tâches pred sont dans les taches pred du ro
        //TODO : si oui, la tâche est valide

        //TODO : l'objet à se trimbaler c'est la liste des prédécesseurs restant
        //TODO et juste check si vide à la fin

        for (int i = 0; i < tasks.length; i++) {
            for (int j = 0; j < tasks[i].length; j++) {
                Task t = tasks[i][j];

                if (pred_tasks_ro.contains(t)) {
                    nb_tasks_left--;
                }
            }
        }


        return (nb_tasks_left == 0);
    }

    public ArrayList<Task> getPredecessorsInResourceOrder(ResourceOrder resourceOrder) {


        Task[][] tasks = resourceOrder.tasksByMachine;
        ArrayList<Task> predecessors = new ArrayList<>();

        for (int i = 0; i < tasks.length; i++) {
            for (int j = 0; j < tasks[i].length; j++) {
                Task t = tasks[i][j];
                if (t.job == this.job && t.task < this.task) {
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
