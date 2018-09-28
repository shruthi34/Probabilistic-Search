package probSearch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class MovingTarget {

	//References Throughout Code :
	//1 - Flat
	//2 - Hill
	//3 - Forest
	//4 - Cave
	
	
	static HashMap<Integer , Integer[]> ab = new HashMap<Integer , Integer[]>(); 
	static int n = 50, targetI, targetJ, maxI, maxJ, maxArea;
	static double prob[][] = new double[n][n]; //probability
	static double probFind[][] = new double[n][n]; //probability for rule 2
	static double tempProb[][] = new double[n][n]; 
	static int probStore[][] = new int[n][n]; //landscape of each area
	static double falseNeg[] = {0.1 , 0.3 , 0.7 , 0.9}; //false negative for 4 landscape types
	static double cellProb[] = {0.2 , 0.3 , 0.3 , 0.2};
	static HashSet<Integer> transition = new HashSet<Integer>(); //stores boundary transitions landscape types
	static HashMap<Integer , Integer> neighbors = new HashMap<Integer ,Integer>(); 
	static double falseN;
	
	static Random random = new Random();
	
	
	public static void main(String[] args) 
	{
		int stepSum = 0 , t;
		
		//running moving target for 100 iterations
		for(t = 0 ; t < 100 ; t++)
		{
			
			//creating random landscape
			for(int i = 0 ; i < n ; i++)
			{
				for(int j = 0 ; j < n ; j++)
				{
					prob[i][j] = 1.0/Math.pow(n, 2); 
					
					double a = random.nextDouble();
					if(a < 0.2)
					{
						probStore[i][j] = 1;
					}
					else if(a < 0.5)
					{
						probStore[i][j] = 2;
					}
					else if(a < 0.8)
					{
						probStore[i][j] = 3;
					}
					else
					{
						probStore[i][j] = 4;
					}
				}
			}
			
			//target initial position
			targetI = random.nextInt(n);
			targetJ = random.nextInt(n);
			
			
			//choosing first start search position randomly
			maxI = random.nextInt(n);
			maxJ = random.nextInt(n);
			
			int stepCount = 0;
			
			while(true)
			{
				
				maxArea = probStore[maxI][maxJ];
				falseN = falseNeg[maxArea - 1];
			
				stepCount++;
				
				
				//check target location with search location
				if(maxI == targetI && maxJ == targetJ)
				{
					double check = random.nextDouble();
					
					//checking false negative
					if(check > falseN)
					{
						System.out.println("Target found at " + maxI + " " + maxJ);
						System.out.println("Steps : " + stepCount);
						initializeProb();
						stepSum = stepSum + stepCount;
						break;
					}
					
					//if we cannot reach target due to false negative
					else
					{
						changeProb(); //change all probabilities when target not found					
						
						checkTransition(); //get boundary transitions
						
						changeTransitionProb(); //change probabilities based on boundary transactions
						
						//Rule 1 : 
						getMaxIJ();
						
						//Rule 2: [Uncomment this part to run instead of getMaxIJ]
						//getFindMaxIJ();						
					}
				}
				else
				{			
					changeProb();	//change all probabilities when target not found				
					
					checkTransition(); //get boundary transitions
					
					changeTransitionProb(); //change probabilities based on boundary transactions
					
					
					//Rule 1 : 
					getMaxIJ();
					
					//Rule 2: [Uncomment this part to run instead of getMaxIJ]
					//getFindMaxIJ();
				}
			}
		}
		

		System.out.println(stepSum);
		t--;
		System.out.println("Average steps : " + (stepSum/(t)));
			
		}
		
	public static void initializeProb()
	{
		for(int x = 0 ; x < n ; x++)
		{
			for(int y = 0 ; y < n ; y++)
			{
				prob[x][y] = 1.0/Math.pow(n, 2);
			}
		}
	}
	
	
	//finding next search location based on Rule 1
	static void getMaxIJ()
	{
		double max = 0;
		
		for(int i = 0 ; i < n ; i++)
		{
			for(int j = 0 ; j < n ; j++)
			{
				if(prob[i][j] > max)
				{
					maxI = i;
					maxJ = j;
					max = prob[i][j];
				}
			}
		}
		
	}
	
	
	//finding next search location based on Rule 2
	static void getFindMaxIJ()
	{
		double max = 0;
		
		for(int i = 0 ; i < n ; i++)
		{
			for(int j = 0 ; j < n ; j++)
			{
				probFind[i][j] = prob[i][j]*(1-falseNeg[probStore[i][j]-1]);
			}
		}
		
		for(int i = 0 ; i < n ; i++)
		{
			for(int j = 0 ; j < n ; j++)
			{
				if(probFind[i][j] > max)
				{
					maxI = i;
					maxJ = j;
					max = probFind[i][j];
				}
			}
		}
		
	}
	
	
	//Change probability based on search location and false negative for that location 
	static void changeProb()
	{
		double normFactor = prob[maxI][maxJ]*falseN + 1 - prob[maxI][maxJ];
		
		for(int k = 0 ; k < n ; k++)
		{
			for(int l = 0 ; l < n ; l++)
			{
				if(k == maxI && l == maxJ)
				{
					prob[k][l] = prob[maxI][maxJ]*falseN/normFactor;
				}
				else
				{
					prob[k][l] = prob[k][l]/normFactor;
						
				}							
				
			}
		}
	}
	
	
	
	//getting next random transition boundary & changin target location
	static void checkTransition()
	{
		transition.clear();
		transition.add(probStore[targetI][targetJ]);
		
		neighbors.clear();
		
		Integer a[];
		
		int i = 0;
		
		if(targetI - 1 >= 0)
		{
			a = new Integer[2];
			a[0] = targetI - 1;
			a[1] = targetJ;
			ab.put(i , a);
			neighbors.put(i	, probStore[targetI - 1][targetJ]);
			i++;
		}
		if(targetI + 1 < n)
		{
			a = new Integer[2];
			a[0] = targetI + 1;
			a[1] = targetJ;
			ab.put(i , a);
			neighbors.put(i	, probStore[targetI + 1][targetJ]);
			i++;
		}
		if(targetJ - 1 >= 0)
		{
			a = new Integer[2];
			a[0] = targetI;
			a[1] = targetJ - 1;
			ab.put(i , a);
			neighbors.put(i	, probStore[targetI][targetJ - 1]);
			i++;
		}
		if(targetJ + 1 < n)
		{
			a = new Integer[2];
			a[0] = targetI;
			a[1] = targetJ + 1;
			ab.put(i , a);
			neighbors.put(i	, probStore[targetI][targetJ + 1]);
			i++;
		}
		
		int k = random.nextInt(i);
		transition.add(neighbors.get(k));
		
		Integer k1[] = ab.get(k);
		
		targetI = k1[0];
		targetJ = k1[1];
		
							
	}
	
	
	//change probabilities based on transition boundary values
	static void changeTransitionProb()
	{
		double sum = 0;
		for(int i = 0 ; i < n ; i++)
		{
			for(int j = 0 ; j < n ; j++)
			{
				if(transition.contains(probStore[i][j]))
				{
					if(transition.size() == 2)
					{
						transition.remove(probStore[i][j]);
					}
					
					double newProb = 0;
					if(i - 1 >= 0)
					{
						if(transition.contains(probStore[i - 1][j]))
						{
							newProb = newProb + prob[i - 1][j];
						}
					}
					if(i + 1 < n)
					{
						if(transition.contains(probStore[i + 1][j]))
						{
							newProb = newProb + prob[i + 1][j];
						}
					}
					if(j - 1 >= 0)
					{
						if(transition.contains(probStore[i][j - 1]))
						{
							newProb = newProb + prob[i][j - 1];
						}
					}
					if(j + 1 < n)
					{
						if(transition.contains(probStore[i][j + 1]))
						{
							newProb = newProb + prob[i][j + 1];
						}
					}
					
					tempProb[i][j] = newProb;
					
					transition.add(probStore[i][j]);
				}
				else
				{
					tempProb[i][j] = 0;
				}
				
				sum = sum + tempProb[i][j];
				
			}
		}
		for(int i = 0 ; i < n ; i++)
		{
			for(int j = 0 ; j < n ; j++)
			{
				prob[i][j] = tempProb[i][j]/sum;
			}
		}
		
		
		
	}
}
