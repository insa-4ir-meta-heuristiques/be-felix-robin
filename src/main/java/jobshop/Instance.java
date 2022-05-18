package jobshop;

import jobshop.encodings.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.stream.Collectors;

/** Represents an instance of a JobShop problem. */
public final class Instance {

    /** Name of the instance. Same as the filename from which the instance is loaded. */
    public final String name;

    /** Number of jobs in the instance */
    public final int numJobs;

    /** Number of tasks per job */
    public final int numTasks;

    /** Number of machines, assumed to be same as number of tasks. */
    public final int numMachines;

    /** Matrix containing the duration of all tasks. */
    final int[][] durations;

    /** Matrix containing the machine on which each task must be scheduled. */
    final int[][] machines;

    /** Duration of the given task. */
    public int duration(int job, int task) {
        return durations[job][task];
    }

    /** Duration of the given task. */
    public int duration(Task t) {
        return duration(t.job, t.task);
    }

    /** Machine on which the given task must be scheduled. */
    public int machine(int job, int task) {
        return machines[job][task];
    }

    /** Machine on which the given task must be scheduled. */
    public int machine(Task t) {
        return this.machine(t.job, t.task);
    }

    /** Among the tasks of the given job, returns the task number of the one that uses the given machine. */
    public int task_with_machine(int job, int wanted_machine) {
        for(int task = 0 ; task < numTasks ; task++) {
            if(machine(job, task) == wanted_machine)
                return task;
        }
        throw new RuntimeException("No task targeting machine "+wanted_machine+" on job "+job);
    }

    /**
     * Creates a new instance, with uninitialized durations and machines.
     * This should no be called directly. Instead, Instance objects should be created with the
     * <code>Instance.fromFile()</code> static method.
     */
    Instance(String name, int numJobs, int numTasks) {
        this.name = name;
        this.numJobs = numJobs;
        this.numTasks = numTasks;
        this.numMachines = numTasks;

        durations = new int[numJobs][numTasks];
        machines = new int[numJobs][numTasks];
    }

    /** Returns all tasks in the instance.
     * They are impossible by default.
     * Adds the next_task to every task.
     */
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> all_tasks = new ArrayList<>();

        for (int j = this.numJobs-1; j >= 0 ; j--) {

            // The task that follows
            Task next = null;

            for (int i = this.numTasks-1; i >= 0 ; i--) {

                Task t = new Task(j, i);
                if (i == 0) {
                    t.is_possible = true;
                    t.next_task = next;
                }
                // If the task isn't the last one
                // Then there is a next that isn't null
                else if (i < this.numTasks-1) {
                    t.next_task = next;
                }

                // If it's the last task, we just instantiate next
                // If it's the fist task, updating next is useless
                // Otherwise, we update next to the new value
                next = t;
                all_tasks.add(t);
            }
        }
        return all_tasks;
    }

    /** Parses a instance from a file. */
    public static Instance fromFile(Path path) throws IOException {
        String name = path.getFileName().toString();
        Iterator<String> lines = Files.readAllLines(path).stream()
                .filter(l -> !l.startsWith("#"))
                .collect(Collectors.toList())
                .iterator();

        Scanner header = new Scanner(lines.next());
        int numJobs = header.nextInt();
        int numTasks = header.nextInt();
        Instance pb = new Instance(name, numJobs, numTasks);

        for(int job = 0 ; job<numJobs ; job++) {
            Scanner line = new Scanner(lines.next());
            for(int task = 0 ; task < numTasks ; task++) {
                pb.machines[job][task] = line.nextInt();
                pb.durations[job][task] = line.nextInt();
            }
        }

        return pb;
    }
}
