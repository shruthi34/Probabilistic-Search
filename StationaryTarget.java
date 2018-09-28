package probSearch;

import java.util.HashMap;
import java.util.Random;


public class StationaryTarget {

		//References Throughout Code :
		//1 - Flat
		//2 - Hill
		//3 - Forest
		//4 - Cave
	
	static HashMap<Integer, Integer> stepsCounts = new HashMap<Integer , Integer>();
	static int searchCounts[] = new int[4];
	static int manhCost[] = new int[4];
	
	
	static int n = 50 , target_i , target_j, max_i , max_j, manhCostV = 0;
	static double prob[][] = new double[n][n]; //probability
	static double probFind[][] = new double[n][n]; //probability for rule 2
	static int probStore[][] = new int[n][n]; //stores boundary transitions landscape types
	static double falseNeg[] = {0.1 , 0.3 , 0.7 , 0.9}; //false negative for 4 landscape types 
	static double cellProb[] = {0.2 , 0.3 , 0.3 , 0.2};
	static int probStoreCount[] = new int[4];
	
	static int itCount = 0;
	
	public static void main(String[] args) {
		
		//100 iterations -> 25 for each landscape type
		for(int t = 0 ; t < 100 ; t++)
		{
						
			Random random = new Random();
			
			
			//creating random landscape
			for(int i = 0 ; i < n ; i++)
			{
				for(int j = 0 ; j < n ; j++)
				{
					prob[i][j] = 1.0/Math.pow(n, 2); 
					
					double a = random.nextDouble();
					if(a < 0.2)
					{
						probStoreCount[0]++;
						probStore[i][j] = 1;
					}
					else if(a < 0.5)
					{
						probStoreCount[1]++;
						probStore[i][j] = 2;
					}
					else if(a < 0.8)
					{
						probStoreCount[2]++;
						probStore[i][j] = 3;
					}
					else
					{
						probStoreCount[3]++;
						probStore[i][j] = 4;
					}
				}
			}
			
			//placing target randomly
			target_i = random.nextInt(n);
			target_j = random.nextInt(n);
			
			int target;
		
			target = probStore[target_i][target_j];
			
			if(searchCounts[target - 1] < 25)
			{
				System.out.println("Target is:" + target);
				
				//first search location chosen randomly
				max_i = random.nextInt(n);
				max_j = random.nextInt(n);
				
				int stepCount = 0;
				
				int maxArea;
				double falseN, normFactor;
				
				
				while(true)
				{	
					maxArea = probStore[max_i][max_j];
					falseN = falseNeg[maxArea - 1];
				
					stepCount++;
					
					if(max_i == target_i && max_j == target_j)
					{
						double check = random.nextDouble();
						if(check > falseN)
						{
							System.out.println("Target found at " + max_i + " " + max_j);
							System.out.println("Steps : " + stepCount);
							
							//storing step counts for taking average
							if(stepsCounts.get(maxArea) == null)
							{
								stepsCounts.put(maxArea , stepCount);
								searchCounts[maxArea - 1]++;
							}
							else
							{
								int temp = stepsCounts.get(maxArea);
								stepsCounts.put(maxArea, temp + stepCount);
								searchCounts[maxArea - 1]++;
							}
							
							
							//storing Manhattan cost for each landscape type
							manhCost[maxArea - 1] = manhCost[maxArea - 1] + manhCostV;
							manhCostV = 0;
							
							break;
						}
						
						//if target not found due to false negative
						else
						{
							normFactor = prob[max_i][max_j]*falseN + 1 - prob[max_i][max_j];
							
							
							for(int k = 0 ; k < n ; k++)
							{
								for(int l = 0 ; l < n ; l++)
								{
									if(k == max_i && l == max_j)
									{
										prob[k][l] = prob[max_i][max_j]*falseN/normFactor;
									}
									else
									{
										prob[k][l] = prob[k][l]/normFactor;
											
									}							
									
								}
							}
							
							//Rule 1:
							//getMaxIJ();
						
							//Rule : 2
							//getFindMaxIJ();
							
							
							//Rule 1 & 2 for Manhattan
							getMaxIJManh();

							
							
							int x = target_i - max_i;
							int y = target_j - max_j;
							manhCostV = manhCostV + Math.abs(x) + Math.abs(y);
														
							
						}
					}
					else
					{
						normFactor = prob[max_i][max_j]*falseN + 1 - prob[max_i][max_j];
						
						
						for(int k = 0 ; k < n ; k++)
						{
							for(int l = 0 ; l < n ; l++)
							{
								if(k == max_i && l == max_j)
								{
									prob[k][l] = prob[max_i][max_j]*falseN/normFactor;
								}
								else
								{
									prob[k][l] = prob[k][l]/normFactor;
								}
								
							}
						}
						
						//Rule 1:
						//getMaxIJ();
					
						//Rule : 2
						//getFindMaxIJ();
						
						
						//Rule 1 & 2 for Manhattan
						getMaxIJManh();

						
						
						int x = target_i - max_i;
						int y = target_j - max_j;
						manhCostV = manhCostV + Math.abs(x) + Math.abs(y);
						
						
					}
				}
			}
			
			else
			{
				t--;
				continue;
			}
		}
		for(int i = 0 ; i < 4 ; i++)
			{
				if(stepsCounts.get(i+1) != null)
				{
					int avg = stepsCounts.get(i+1)/searchCounts[i];
					System.out.println("Average for target at " + (i+1) + " = " + avg 
							+ " with 10" + " trials");
					
					System.out.println("Average Cost : " + manhCost[i]/searchCounts[i]);
				}
				else
				{
					System.out.println("No targets were placed at " + i + " in " 
								+ searchCounts[i] + " iterations");
				}
				
			}
			
		
	}
	

	//Rule 1
	static void getMaxIJ()
	{
		double max = 0;
		
		for(int i = 0 ; i < n ; i++)
		{
			for(int j = 0 ; j < n ; j++)
			{
				if(prob[i][j] > max)
				{
					max_i = i;
					max_j = j;
					max = prob[i][j];
				}
			}
		}
	}
	
	//Manhattan using rule 1 / rule 2
	static void getMaxIJManh()
	{
		double m = 0  , check;
		int dumI = 0 , dumJ = 0;
		
		int sum = 0;
		
		for(int i = 0 ; i < n ; i++)
		{
			for(int j = 0 ; j < n ; j++)
			{
				if(i != max_i && j != max_j)
				{
					int x = max_i - i;
					int y = max_j - j;
					sum = sum + Math.abs(x) + Math.abs(y);
				}
			}
		}
		
		
		double max = Integer.MIN_VALUE;
		int checkI = 0, checkJ = 0;
		
		//Rule 1
		/*for(int i = 0 ; i < n ; i++)
		{
			for(int j = 0 ; j < n ; j++)
			{
				if(prob[i][j] > max)
				{
					checkI = i;
					checkJ = j;
					max = prob[i][j];
				}
			}
		}*/
		
		
		//Rule 2
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
					checkI = i;
					checkJ = j;
					max = probFind[i][j];
				}
			}
		}
		
		double refMax = (Math.abs(max_i - checkI) + Math.abs(max_j - checkJ));
		
		dumI = checkI;
		dumJ = checkJ;
		
		for(int i = 0 ; i < n ; i++)
		{
			for(int j = 0 ; j < n ; j++)
			{
				if(i != max_i && j != max_j)
				{
					int x = max_i - i;
					int y = max_j - j;
					
					check =  (Math.abs(x) + Math.abs(y));
						
					
					if( m > check && refMax > check)
					{
						if(prob[i][j] >= 0.9*max )
						{
							dumI = i;
							dumJ = j;
							m = check;
						}
						
					}
				}
				
			}
		}
		
		max_i = dumI;
		max_j = dumJ;
	}
	
	//Rule 2
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
					max_i = i;
					max_j = j;
					max = probFind[i][j];
				}
			}
		}
		
	}

}
