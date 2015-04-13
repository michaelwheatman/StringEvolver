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
		
		/** constructor to create a new individual from a given sequence **/
		public Individual(char[] sequence) {
			sequence = sequence;
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
//		System.out.println("mutating");
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
	
	/** Proportional Selection **/
    public void proportionalSelection() {
		int sumFitness = 0;
		int[] fitnessScores = new int[popSize];
        for (int i=0; i<popSize; i++) {
			sumFitness+=pop[i].fitness;
			fitnessScores[i] = sumFitness;
		}
		
		Individual[] newPop = new Individual[popSize];
       	float gap = (float) sumFitness / popSize;
		int curIndex = 0;
		float stochasticNum;
		for (int i = 0; i < popSize; i++) {
			stochasticNum = gap/2 + i*gap;
			while (fitnessScores[curIndex] < stochasticNum){
				curIndex++;
			}
			newPop[i] = new Individual(pop[curIndex]);
		}
		pop = newPop;
	}
	
	/** Run tournament by shuffling population then selecting first t individuals to compete **/
	public Individual shuffleAndGet(Individual[] population, int t) {
		Collections.shuffle(Arrays.asList(population));
		Individual toReturn = new Individual(population[0]);
		for (int i = 1; i < t; i++) {
			if (population[i].fitness > toReturn.fitness) {
				toReturn = new Individual(population[i]);
			}
		}
		return toReturn;
	}
	
	/** Run tournament by selecting t random individuals to compete **/
	public Individual getFromTournament(Individual[] population, int t) {
		int randInt = rgen.nextInt(popSize);
		Individual toReturn = new Individual(population[randInt]);
		for (int i=1; i < t; i++) {
			randInt = rgen.nextInt(popSize);
			if (population[randInt].fitness > toReturn.fitness) {
				toReturn = new Individual(population[randInt]);
			}
		}
		return toReturn;
	}
	
	/**  Tournament Selection **/
	public void tournamentSelection(int tournamentSize) {
		Individual[] newPop = new Individual[popSize];
		for (int i = 0; i < popSize; i++) {
			//Two methods of running the tournament
			if (popSize < 5) {
				newPop[i] = shuffleAndGet(pop, tournamentSize);
			} else {
				newPop[i] = getFromTournament(pop, tournamentSize);
			}
		} 
		pop = newPop;
	}
	
	public float calcMeanFitness() {
		int totalFitness = 0;
		for (int i = 0; i < popSize;  i++) {
			totalFitness+=pop[i].fitness;
		}
		return (float) totalFitness/popSize;
	}
	
	/**  One Point Crossover **/
	public void crossoverOne() {
		Individual[] newPop = new Individual[popSize];
		Collections.shuffle(Arrays.asList(pop));
		int splitPoint;
		Individual ind1;
		Individual ind2;
		Individual new1;
		Individual new2;
		for (int i = 0; i<popSize-1; i+=2) {
			splitPoint = rgen.nextInt(24) + 1;
			ind1 = pop[i];
			ind2 = pop[i+1];
			new1 = new Individual(26);
			new2 = new Individual(26);
			for (int j = 0; j < splitPoint; j++) {
				new1.sequence[j] = ind1.sequence[j];
				new2.sequence[j] = ind2.sequence[j];
			}
			for (int j = splitPoint; j < 26; j++) {
				new2.sequence[j] = ind1.sequence[j];
				new1.sequence[j] = ind2.sequence[j];
			}
			newPop[i] = new1;
			newPop[i+1] = new2;
		}
		pop = newPop;
	}
	
	/**  Uniform Crossover **/
	public void crossoverUniform() {
		Individual[] newPop = new Individual[popSize];
		Collections.shuffle(Arrays.asList(pop));
		Individual ind1;
		Individual ind2;
		Individual new1;
		Individual new2;
		int rand;
		for (int i = 0; i<popSize-1; i+=2) {
			ind1 = pop[i];
			ind2 = pop[i+1];
			new1 = new Individual(26);
			new2 = new Individual(26);
			for (int j = 0; j < 26; j++) {
				rand = rgen.nextInt(2);
				if (rand == 0) {
					new2.sequence[j] = ind1.sequence[j];
					new1.sequence[j] = ind2.sequence[j];
				} else if (rand == 1) {
					new1.sequence[j] = ind1.sequence[j];
					new2.sequence[j] = ind2.sequence[j];
				} else {
					System.out.println("Rand Error");
				}
			}
			newPop[i] = new1;
			newPop[i+1] = new2;
		}
		pop = newPop;
	}

    public static void main(String[] args) {
	
        if (args.length<3) {
            System.out.println("must include population size, crossover type, and selection type as arguments");
            return;
        }
        // get population size as command line argument and create new GA
        int popSize = Integer.parseInt(args[0]);
		int crossoverType = Integer.parseInt(args[1]); //0=None, 1=1-point, 2=uniform
		int selectionType = Integer.parseInt(args[2]); //0=Proportional, 1=Tournament
		int tournamentSize = 0;
		
		String crossoverName;
		String selectionName;
		
		if (selectionType == 1) {
			if (args.length<4) {
            	System.out.println("must include tournament size if using tournament selection");
            	return;
        	}
			tournamentSize = Integer.parseInt(args[3]);
			selectionName = "Tournament";
		} else {
			selectionName = "Proportional";
		}
		
		if (crossoverType == 0) {
			crossoverName = "None";
		} else if (crossoverType == 1) {
			crossoverName = "One Point";
		} else {
			crossoverName = "Uniform";
		}
		
		long lStartTime = new Date().getTime();
		
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

				//Selection
				if (selectionType == 0) {
					SGA.proportionalSelection();
				} else if (selectionType == 1) {
					SGA.tournamentSelection(tournamentSize);
				}
				
				//Crossover
				if (crossoverType == 1) {
					SGA.crossoverOne();
				} else if (crossoverType == 2) {
					SGA.crossoverUniform();
				}
                SGA.mutate();
                gens++;
            }
            
            totGens += gens;
            if (gens < minGens) minGens = gens;
            if (gens > maxGens) maxGens = gens;
        }
		long lEndTime = new Date().getTime();
		long difference = lEndTime - lStartTime;
		
        // print final data
		System.out.println("Population Size: " + popSize + ", Crossover Type: " + crossoverName + ", Selection Type: " + selectionName + ", Tournament Size: " + tournamentSize + ", ave gens: "+totGens/40.0+", range: "+minGens + " to " + maxGens + ", Work: " + popSize*totGens/40.0 + ", Elapsed milliseconds: " + difference + ".");
    }
}