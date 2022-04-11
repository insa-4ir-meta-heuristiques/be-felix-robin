package jobshop.solvers.neighborhood;

import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.encodings.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/** Implementation of the Nowicki and Smutnicki neighborhood.
 *
 * It works on the ResourceOrder encoding by generating two neighbors for each block
 * of the critical path.
 * For each block, two neighbors should be generated that respectively swap the first two and
 * last two tasks of the block.
 */
public class Nowicki extends Neighborhood {

    /** A block represents a subsequence of the critical path such that all tasks in it execute on the same machine.
     * This class identifies a block in a ResourceOrder representation.
     *
     * Consider the solution in ResourceOrder representation
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (0,2) (2,1) (1,1)
     * machine 2 : ...
     *
     * The block with : machine = 1, firstTask= 0 and lastTask = 1
     * Represent the task sequence : [(0,2) (2,1)]
     *
     * */
    public static class Block {
        /** machine on which the block is identified */
        public final int machine;
        /** index of the first task of the block */
        public final int firstTask;
        /** index of the last task of the block */
        public final int lastTask;

        /** Creates a new block. */
        Block(int machine, int firstTask, int lastTask) {
            this.machine = machine;
            this.firstTask = firstTask;
            this.lastTask = lastTask;
        }

        public String toString() {
            return "Machine "+this.machine+" : "+this.firstTask+"-"+this.lastTask;
        }
    }

    /**
     * Represents a swap of two tasks on the same machine in a ResourceOrder encoding.
     *
     * Consider the solution in ResourceOrder representation
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (0,2) (2,1) (1,1)
     * machine 2 : ...
     *
     * The swap with : machine = 1, t1= 0 and t2 = 1
     * Represent inversion of the two tasks : (0,2) and (2,1)
     * Applying this swap on the above resource order should result in the following one :
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (2,1) (0,2) (1,1)
     * machine 2 : ...
     */
    public static class Swap {
        /** machine on which to perform the swap */
        public final int machine;

        /** index of one task to be swapped (in the resource order encoding).
         * t1 should appear earlier than t2 in the resource order. */
        public final int t1;

        /** index of the other task to be swapped (in the resource order encoding) */
        public final int t2;

        /** Creates a new swap of two tasks. */
        Swap(int machine, int t1, int t2) {
            this.machine = machine;
            if (t1 < t2) {
                this.t1 = t1;
                this.t2 = t2;
            } else {
                this.t1 = t2;
                this.t2 = t1;
            }
        }

        public String toString() {

            return "Machine "+this.machine+" : "+this.t1+"<->"+this.t2;
        }

        /** Creates a new ResourceOrder order that is the result of performing the swap in the original ResourceOrder.
         *  The original ResourceOrder MUST NOT be modified by this operation.
         */
        public ResourceOrder generateFrom(ResourceOrder original) {


            ResourceOrder ro = original.copy();
            ro.swapTasks(this.machine, this.t1, this.t2);

            if (ro.toSchedule().isPresent()) {
                return ro;
            }
            else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Swap swap = (Swap) o;
            return machine == swap.machine && t1 == swap.t1 && t2 == swap.t2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(machine, t1, t2);
        }
    }


    @Override
    public List<ResourceOrder> generateNeighbors(ResourceOrder current) {
        // convert the list of swaps into a list of neighbors (function programming FTW)
        return allSwaps(current).stream().map(swap -> swap.generateFrom(current)).collect(Collectors.toList());

    }

    /** Generates all swaps of the given ResourceOrder.
     * This method can be used if one wants to access the inner fields of a neighbors. */
    public List<Swap> allSwaps(ResourceOrder current) {
        List<Swap> neighbors = new ArrayList<>();
        // iterate over all blocks of the critical path
        for(var block : blocksOfCriticalPath(current)) {
            // for this block, compute all neighbors and add them to the list of neighbors
            neighbors.addAll(neighbors(block));
        }
        return neighbors;
    }

    /** Returns a list of all the blocks of the critical path. */
    public List<Block> blocksOfCriticalPath(ResourceOrder order) {

        List<Block> result = new ArrayList<>();

        Optional<Schedule> s = order.toSchedule();
        List<Task> criticalPath;

        if (s.isPresent()) {
            criticalPath = s.get().criticalPath();
        }
        else {
            throw new UnsupportedOperationException();
        }

        int machine_avant = order.instance.machine(criticalPath.get(0));
        int machine_ici;
        int first_task = 0;
        boolean first = true;

        for (int i = 1; i < criticalPath.size(); i++) {
            machine_ici = order.instance.machine(criticalPath.get(i));

            //System.out.println(i + "/"+(criticalPath.size()-1));
            //System.out.println(machine_avant + " vs " + machine_ici);
            //System.out.println(first);

            // Beginning of a chain
            if (machine_ici == machine_avant && first) {
                first_task = i-1;
                first = false;

                if (i == criticalPath.size()-1) {
                    result.add(new Block(machine_avant, first_task, i));
                }
            }

            // If first is false then we are in a chain
            // We remember the beginning task with first
            // If the current machine and the previous machine re different, it's the end of the chain
            // Then we can add a block with first and previous

            else if (!first && machine_ici != machine_avant) {
                result.add(new Block(machine_avant, first_task, i-1));
                first = true;
            }

            else {
                machine_avant = machine_ici;
            }


        }
        //System.out.println("/// critical path ///");
        //System.out.println(criticalPath);

        return result;
    }

    /** For a given block, return the possible swaps for the Nowicki and Smutnicki neighborhood */
    public List<Swap> neighbors(Block block) {

        List<Swap> l = new ArrayList<>();

        Swap s1 = new Swap(block.machine, block.firstTask+1, block.firstTask);
        l.add(s1);

        if (block.lastTask - block.firstTask > 2) {

            Swap s2 = new Swap(block.machine, block.lastTask, block.lastTask-1);
            l.add(s2);
        }

        return l;
    }

}
