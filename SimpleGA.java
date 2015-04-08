/**
 * Partial code for a simple GA to evolve matching strings.
 * Author: Sherri Goings
 * Last Modified: 4/10/13
 **/
import java.util.*;

public class SimpleGA {
    // GA parameters, you should not change those that are declared final
    // except perhaps while testing.
    private int popSize;
    private final double mutRate = .5;
    private final int stringLength = 26;
    private Individual[] pop;
    private final String goal = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    Random rgen;

    /** Class to represent a single individual in GA population **/
    private class Individual {
        private char[] sequence;  // genome
        private int fitness;

        /** constructor creates empty genome and initializes fitness to 0 **/
        public Individual(int length) {
            sequence = new char[length];
            fitness = 0;
        }

        /** copy constructor to create a new individual from an existing one **/
        public Individual(Individual copy) {
            sequence = Arrays.copyOf(copy.sequence, copy.sequence.length);
            fitness = copy.fitness;
        }
        
        /** initialize a genome with new random characters **/
        public void fillRandom(Random rgen) {
            for (int i =0; i<sequence.length; i++) 
                sequence[i] = (char)(rgen.nextInt(26) + 65);
        }

        /** set fitness of individual to how many chars match the goal string **/
        public void evalMatch(String goal) {
            fitness = 0;
            for (int i = 0; i<sequence.length; i++) 
                // note this is comparing 2 chars, a primitive type in Java so == is ok
                if (sequence[i]==goal.charAt(i)) fitness++;
            System.out.println(Arrays.toString(sequence) + " " + fitness);
        }

        /** mutate by changing one char to a random char if probability is met **/
        public void mutate(double mutRate) {
            if (rgen.nextDouble() < mutRate) {
                int mutIndex = rgen.nextInt(sequence.length);
                sequence[mutIndex] = (char)(rgen.nextInt(26) + 65);
            }
        }

        /** print an individual's genome and fitness **/
        public String toString() {
            String s = "";
            for (int i =0; i<sequence.length; i++) 
                s += sequence[i];
            return "("+s+", "+fitness+")";
        }
        
    }
    
    /** Set up GA with main parameters and goal string to match **/
    public SimpleGA(int pSize) {
        popSize = pSize;
        pop = new Individual[popSize];
        rgen = new Random();
    }

    /** fill population with random individuals **/
    public void initPopulation() {
        // fill pop with random individuals, ASCII for 'A' is 65, so 
        // each char is converted value between 65 and 90
        for (int i=0; i<popSize; i++) {
            pop[i] = new Individual(stringLength);
            pop[i].fillRandom(rgen);
        }
    }

    /** determine fitness of all individuals in population **/
    public void evaluateAll() {
        for (int i=0; i<popSize; i++) 
            pop[i].evalMatch(goal);
    }     

    /** return true if perfect match has been found, false otherwise **/
    public boolean solved() {
        for (int i=0; i<popSize; i++) 
            if (pop[i].fitness >= stringLength) return true;
        return false;
    }

    /** mutate all individuals in population **/
    public void mutate() {
        for (int i=0; i<popSize; i++) 
            pop[i].mutate(mutRate);        
    }

    /** print all individuals in the population **/
    public void printPopulation() {
        System.out.println("current population:");
        for (int i=0; i<popSize; i++) 
            System.out.println(pop[i]);
        System.out.println();
    }
    
    public static void main(String[] args) {
        if (args.length<1) {
            System.out.println("must include population size as argument");
            return;
        }
        // get population size as command line argument and create new GA
        int popSize = Integer.parseInt(args[0]);
        SimpleGA SGA = new SimpleGA(popSize);

        // test GA by performing 40 runs with current parameters and determining
        // resulting average, min, and max number of generations to find a perfect match.
        // Cut runs of at 50,000 generations if haven't found a match yet.
        int totGens = 0;
        int minGens = 50000;
        int maxGens = 0;

        for (int i=0; i<40; i++) {
            SGA.initPopulation();
            int gens = 0;
            while (gens<50000) {
                SGA.evaluateAll();
                if (SGA.solved()) break;

                /***************************
                // TO-DO perform selection
                // TO-DO perform crossover
                ****************************/

                SGA.mutate();
                gens++;
            }
            
            // print number gens for this run, update min and max if appropriate
            System.out.println("gens: "+gens);
            totGens += gens;
            if (gens < minGens) minGens = gens;
            if (gens > maxGens) maxGens = gens;
        }
        // print final data
        System.out.println("\nave gens: "+totGens/40.0+"  range: "+minGens + ", " + maxGens + "\n");
    }
}