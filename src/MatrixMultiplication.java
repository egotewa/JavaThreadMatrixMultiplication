/*
 * 
 * Matrix Multiplication using Threads boosting speedup
 * If using matrix transposition, enter square matrixes dimensions as the number of rows and columns
 * in both matrixes need to be the same
 * 
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

//extending thread class with the concrete,proper properties and methods
class MatrixProduct extends Thread {
      private int[][] A;
      private int[][] B;
      private int[][] C;
      int chunkSizeA;
      int chunkSizeB;
      int numberOfThread;
      int threadCount;
      boolean input;
      boolean quietMode;

      public MatrixProduct(int[][] A,int[][] B,int[][] C,int chunkSizeA, int chunkSizeB, int numberOfThread, int threadCount,boolean inputType,boolean quietMode)
      {
         this.A=A;    
         this.B=B;
         this.C=C;   
         this.chunkSizeA=chunkSizeA;
         this.chunkSizeB=chunkSizeB;
         this.numberOfThread=numberOfThread;
         this.threadCount=threadCount;
         this.input=inputType;
         this.quietMode=quietMode;
      }

     public void run()
     {
    	 //Calendar instance so we can keep track on every thread's execution time
    	 Calendar cal = Calendar.getInstance();
 	  	long t1=cal.getTimeInMillis();
 	  	if(quietMode==false)
 	  	{
    	 System.out.println("Thread "+ numberOfThread + "started.");
 	  	}
 	  	//setting current thread's work bounds (chunks)
    	int threadRowAStart= numberOfThread*chunkSizeA;
 		int threadRowAEnd= (numberOfThread+1)* chunkSizeA-1;
	 		if(numberOfThread==(threadCount-1)) {
	 			threadRowAEnd= A.length-1;
	 		}
 		int threadColumnBStart = numberOfThread*chunkSizeB;
 		int threadColumnBEnd= (numberOfThread+1)* chunkSizeB-1;
	 		if(numberOfThread==(threadCount-1)) {
	 			threadColumnBEnd= B[0].length-1;
	 		}
 
	 		if(quietMode==false)
	 	  	{
		 		System.out.println("Thread number "+ numberOfThread + "will be working on rows " + threadRowAStart + " to " + threadRowAEnd + " from matrix A.");
		 		
		 		System.out.println("Thread number "+ numberOfThread + "will be working on cols " + threadColumnBStart + " to " + threadColumnBEnd + " from matrix B.");
	 	  	}
 			
 		//generating pieces of matrix A by rows in every thread - every thread generates only the rows that it's working on
	 		if(input==true)
	 		{
		 		for(int i=threadRowAStart; i<=threadRowAEnd; i++)
		 		{
		 			//System.out.println("Generating row "+i+ " in matrix A...");
		 			for(int j=0; j<A[0].length; j++)
		 			{
		 				//Using Math.random() tends to slow execution, is here for test purposes
		 				/*A[i][j]=(int) (Math.random() * A[i].length);
				              if(A[i][j]==0) {
				            	  A[i][j]=1;
				              }*/
		 				//ThreadLocalRandom is faster as it is specially crafted for multithreading
		 				A[i][j]=ThreadLocalRandom.current().nextInt();
		 			}
		 		}
	 		}		
	 		
	 		// multiplication
	 		for(int rowA=threadRowAStart; rowA<=threadRowAEnd; rowA++)
	 		{
	 			for(int i=0; i<B.length; i++)
	 			{
	 				for(int j=0; j<B[0].length; j++)
	 				{
	 					//System.out.println("Thread " + numberOfThread + " calculating C[" + rowA + "][" + j + "]...");
	 	 				C[rowA][j]+=A[rowA][i]*B[i][j];
	 				}
	 			}
	 		}
	 		Calendar cal2= Calendar.getInstance();
	        long t2=cal2.getTimeInMillis();
	        if(quietMode==false)
	 	  	{
	        		System.out.println("Thread "+ numberOfThread + " finished for "+ (t2-t1) + " milliseconds.");
	 	  	}
	}
}

 public class MatrixMultiplication {
       public static void main(String[] args)
      {   
    	   //inputType keeps track of what kind of an input we're dealing with (see task requirements- task.pdf
    	   boolean inputType;
    	   //quietMode holds whether control console messages should be shown; if not, then only exec time is shown as a result
    	   boolean quietMode;
    	   //parser used to interpret console parameters given by the user
    	  CommandLineParser parser= new BasicParser();
    	  Options options= new Options();
    	  options.addOption("m", "aRows", true, "Number of rows in matrix A");
    	  options.addOption("n", "aCols", true, "Number of columns in matrix A");
    	  options.addOption("k", "bCols", true, "Number of columns in matrix B");
    	  options.addOption("t", "tasks", true, "Number of threads to run the program");
    	  options.addOption("i", "inputFile", true, "Input file from which input information will be read");
    	  options.addOption("o", "outputFile", true, "Output file in which the result matrix C will be written");
    	  options.addOption("q", "quiet", false, "Quiet Mode");
    	  try {
    		  		CommandLine cmd= parser.parse(options, args);
    		  		if(cmd.hasOption("m")&&cmd.hasOption("n")&&cmd.hasOption("k")&&cmd.hasOption("t"))
    		  		{
    		  		inputType=true;
    		  			if(cmd.hasOption("q"))
    		  			{
    		  				quietMode=true;
    		  			}
    		  			else
    		  			{
    		  				quietMode=false;
    		  			}
		    	  	//creating Calendar instance 
		    	  	Calendar cal = Calendar.getInstance();
		    	  	
		    	  	long t1=cal.getTimeInMillis();
		    	  
		    	  	//getting matrices dimensions
		    	  	
		    	  	int numberOfRowsInMatrixA = Integer.parseInt(cmd.getOptionValue("m"));
		  			int numberOfColumnsInMatrixA = Integer.parseInt(cmd.getOptionValue("n"));
		  			int numberOfColumnsInMatrixB= Integer.parseInt(cmd.getOptionValue("k"));
		  			int numberOfThreads=Integer.parseInt(cmd.getOptionValue("t"));
		  			
		  			//defining chunk sizes
		  			int chunkSizeMatrixA=numberOfRowsInMatrixA/numberOfThreads;
		  			int chunkSizeMatrixB=numberOfColumnsInMatrixB/numberOfThreads;
		  			
		  			//printing information for output matrix
		  			if(quietMode==false)
		  	 	  	{
		  				System.out.println("Result matrix will be with dimensions:"+ numberOfRowsInMatrixA+ " x " + numberOfColumnsInMatrixB);
		  	 	  	}
		  			
			       
			       //creating arrays
			       int[][] A=new int[numberOfRowsInMatrixA][numberOfColumnsInMatrixA];
			       int[][] B=new int[numberOfColumnsInMatrixA][numberOfColumnsInMatrixB];
			       int[][] C=new int[numberOfRowsInMatrixA][numberOfColumnsInMatrixB];
			       
			       //creating threads array
			       MatrixProduct[] myThreads= new MatrixProduct[numberOfThreads];

			     //generating matrix A globally with Math.random  - this is slower than the individual thread generation of matrix A, 
			     // is here for test purposes (to show the slowdown)
		      /* System.out.println("Generating matrix A...");
		        for(int i=0; i<numberOfRowsInMatrixA; i++)
		         {
		          for(int j=0;j<numberOfColumnsInMatrixA;j++)
		          {
		        	  //generating random numbers*number of rows in matrix A
		              A[i][j]=(int) (Math.random() * A[i].length);
		              if(A[i][j]==0) {
		            	  A[i][j]=1;
		              }
		          }
		         }   */ 
			       
			    //printing matrixA   - test purposes
		       /* System.out.println("Matrix A:");
		        for(int i=0; i<numberOfRowsInMatrixA; i++)
		        {
		         for(int j=0;j<numberOfColumnsInMatrixA;j++)
		         {
		        	 System.out.print(A[i][j]+ " ");
		         }
		         System.out.println();
		        }   */   
			       
			      if(inputType==true)
			      {
			    	  if(quietMode==false)
				      {
				         System.out.println();    
				         System.out.println("Generating matrix B...");
				         System.out.println();
				      } 
				          for(int i=0; i<numberOfColumnsInMatrixA ;i++)
				          {
				           for(int j=0; j<numberOfColumnsInMatrixB; j++)
				            {
				        	   //generating random numbers*number of columns in matrix B with Math.Random() ; Math.Random() tends to slow down the process
				        	   		/*B[i][j]=(int) (Math.random() * B.length);
				        	   			if(B[i][j]==0) {
				        	   					B[i][j]=1;
				        	   				}*/
				        	   		//generationg random numbers with ThreadLocalRandom
				    	            	B[i][j]=ThreadLocalRandom.current().nextInt();
				            }        
				          }
			      }
			      
			      //printing matrix B -debug purposes
		          /*System.out.println("Matrix B:");
		          for(int i=0; i<numberOfColumnsInMatrixA; i++)
		          {
		           for(int j=0;j<numberOfColumnsInMatrixB;j++)
		           {
		          	 System.out.print(B[i][j]+ " ");
		           }
		           System.out.println();
		          }   */
		          
		          
		   
		         //Threads creation and starting		          
		          for(int i = 0; i < numberOfThreads; i++) {
		        	  myThreads[i]=new MatrixProduct(A,B,C,chunkSizeMatrixA,chunkSizeMatrixB,i,numberOfThreads,inputType,quietMode);
		        	  myThreads[i].start();
		        	  if(quietMode==false)
		        	  {
		        		  System.out.println("Creating thread number "+ i);
		        	  }
		  		}
		        

		          for(int i = 0; i < numberOfThreads; i++) {
		                try{
		                    myThreads[i].join();
		                }
		            catch(InterruptedException e){}
		          }      
		          
		          
		          //test purposes:
		          /*System.out.println("Matrix A:");
		          for(int i=0; i<numberOfRowsInMatrixA; i++)
		          {
		           for(int j=0;j<numberOfColumnsInMatrixA;j++)
		           {
		          	 System.out.print(A[i][j]+ " ");
		           }
		           System.out.println();
		          }
		        
		        System.out.println();
		        System.out.println("Result Matrix:");
		        System.out.println();
		        
		        
		        for(int i=0; i<numberOfRowsInMatrixA; i++)
		        {
		            for(int j=0; j<numberOfColumnsInMatrixB; j++)
		            {
		                System.out.print(C[i][j]+" ");
		            }    
		            System.out.println();            
		        }  */
		        
		          //getting the exec time of the whole program
		        Calendar cal2= Calendar.getInstance();
		        long t2=cal2.getTimeInMillis();
		        System.out.println("Execution time: " + (t2-t1));
    		  		}
    		  		
    		  		
    		  		//the code that runs when the user decides to do matrix multiplication using files
    		  		else if(cmd.hasOption("i")&&cmd.hasOption("t"))
    		  		{
    		  			boolean writeInFile;
    		  			if(cmd.hasOption("o"))
    		  			{
    		  				writeInFile=true;
    		  			}
    		  			else
    		  			{
    		  				writeInFile=false;
    		  			}
    		  			inputType=false;
    		  			if(cmd.hasOption("q"))
    		  			{
    		  				quietMode=true;
    		  			}
    		  			else
    		  			{
    		  				quietMode=false;
    		  			}
					    		  			int numberOfThreads=Integer.parseInt(cmd.getOptionValue("t"));
					    		  			Calendar cal = Calendar.getInstance();
					    		    	  	long t1=cal.getTimeInMillis();
					    		  			BufferedReader in = null;
					    		  		    int numberOfRowsInMatrixA = 0;
					    		  		    int numberOfColumnsInMatrixA = 0;
					    		  		    int numberOfColumnsInMatrixB = 0;
					    		  		    int chunkSizeMatrixA=0;
					    		  		    int chunkSizeMatrixB=0;
					    		  		    int numberOfLines=0;
			    		  		            String filepath = cmd.getOptionValue("i");
			    		  		            String outputFile = cmd.getOptionValue("o");
					    		  		    int [][] A = null;
					    		  		    int [][] B = null;
					    		  		    int [][] C= null;
					    		  		        try {
					    		  		            int lineNum = 0;
					    		  		            int tempRowA=0;
					    		  		            int tempRowB=0;
					    		  		            //reading from the given file
					    		  		            in = new BufferedReader(new FileReader(filepath));
					    		  		            String line = null;
					    		  		            while((line=in.readLine()) !=null) {
					    		  		                lineNum++;
					    		  		                if(lineNum==1) {
					    		  		                	String [] fileArgs = line.split(" ");
					    		  		                    numberOfRowsInMatrixA = Integer.parseInt(fileArgs[0]);
					    		  		                    numberOfColumnsInMatrixA = Integer.parseInt(fileArgs[1]);
					    		  		                    numberOfColumnsInMatrixB= Integer.parseInt(fileArgs[2]);
					    		  		                    numberOfLines=numberOfRowsInMatrixA + numberOfColumnsInMatrixA +1;
					    		  		                    A = new int[numberOfRowsInMatrixA][numberOfColumnsInMatrixA];
					    		  		                    B= new int [numberOfColumnsInMatrixA][numberOfColumnsInMatrixB];
					    		  		                    C= new int [numberOfRowsInMatrixA][numberOfColumnsInMatrixB];
					    		  		                    //defining chunk sizes
					    		  				  			chunkSizeMatrixA=numberOfRowsInMatrixA/numberOfThreads;
					    		  				  			chunkSizeMatrixB=numberOfColumnsInMatrixB/numberOfThreads;	
					    		  				  			//printing information for output matrix
					    		  				  			System.out.println("Result matrix will be with dimensions:"+ numberOfRowsInMatrixA+ " x " + numberOfColumnsInMatrixB);
					    		  				  			System.out.println();
					    		  		                }
					    		  		                
					    		  		              else {
					    		  	                    String [] tokens = line.split(" ");
					    		  	                    	if(lineNum>1 && lineNum<=numberOfLines-numberOfColumnsInMatrixA)
					    		  	                    	{
					    		  	                    		for (int j=0; j<tokens.length; j++) {
					    		  	    	                        A[tempRowA][j] = Integer.parseInt(tokens[j]);
					    		  	                    		}
					    		  	                    		tempRowA++;
					    		  	                    	}
					    		  	                    
					    		  	                    	if(lineNum>numberOfLines-numberOfColumnsInMatrixA && lineNum<=numberOfLines)
					    		  	                    	{
					    		  	                    		for (int j=0; j<tokens.length; j++) {
					    		  	    	                        B[tempRowB][j] = Integer.parseInt(tokens[j]);
					    		  	                    		}
					    		  	                    		tempRowB++;
					    		  	                    	}
					    		  	                 	}
					    		  		          } 
					    		  		            
					    		  		          MatrixProduct[] myThreads= new MatrixProduct[numberOfThreads];
					    		  		          
					    		  		          for(int i = 0; i < numberOfThreads; i++) {
					    				        	  myThreads[i]=new MatrixProduct(A,B,C,chunkSizeMatrixA,chunkSizeMatrixB,i,numberOfThreads,inputType,quietMode);
					    				        	  myThreads[i].start();
					    				        	  System.out.println("Creating thread number "+ i);
					    				  				}
					    		  		          
					    		  		          
					    		  		        for(int i = 0; i < numberOfThreads; i++) {
					    		  		        		try{
					    		  		        			myThreads[i].join();
					    		  		        			}catch(InterruptedException e){}
					    		  		        		}      
					    		  		        
					    		  		        
					    		  		            //printing matrices from file to the console -if needed; debug purposes
					    				            /*for(int i=0;i<numberOfRowsInMatrixA;i++)
					    				            {
					    				            	for(int j=0;j<numberOfColumnsInMatrixA;j++)
					    				            	{
					    				            		System.out.print(A[i][j]+ " " );
					    				            	}
					    				            	System.out.println(" ");
					    				            }
					    				            System.out.println(" ");System.out.println(" ");
					    				            for(int i=0;i<numberOfColumnsInMatrixA;i++)
					    				            {
					    				            	for(int j=0;j<numberOfColumnsInMatrixB;j++)
					    				            	{
					    				            		System.out.print(B[i][j]+ " " );
					    				            	}
					    				            	System.out.println(" ");
					    				            }*/
					    			            
					    				            
					    			        } catch (Exception ex) {
					    			            System.out.println("Code executed unsuccessfully");
					    			            System.out.println(ex.getMessage());
					    			        }
					    		  		  
					    		  	//there is an option to choose whether the user wants to write the result to 
					    		  	//a file with a specific name or not; if yes then this code is executed:
					    		  	if(writeInFile==true) 
					    		  	{
					    		  		      Writer writer=null;
											try {
												writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"));
											} catch (UnsupportedEncodingException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											} catch (FileNotFoundException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
					  	            		for(int i=0;i<numberOfRowsInMatrixA;i++)
					  			            {
					  			            	for(int j=0;j<numberOfColumnsInMatrixB;j++)
					  			            	{
					  			            		try {
														writer.write(C[i][j] + " ");
													} catch (IOException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
					  			            	}
					  			            	try {
													writer.append("\r\n");
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
					  			            }
					  	            		try {
												writer.close();
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} 
					    		  	}
					    		  		     Calendar cal2= Calendar.getInstance();
					    				     long t2=cal2.getTimeInMillis();
				    				        System.out.println("Execution time: " + (t2-t1));
    		  		
    		  		
    		  		
    		  		//has option -i -o bracket
    		  		}
    		  		
    		  		
    		  		//if the inputType does not recognise any of the 2 input types, a message is displayed to the user
    		  		else
    		  		{
    		  			HelpFormatter formatter=new HelpFormatter();
    		  			formatter.printHelp("Seems like you've set invalid parameters. Parameters must be combined in one of two ways: m,n,k,t or i,o,t. Here's some information on how you can set them and what each one of them mean:",options);
    		  		}
    	  } 
    	  	//catch exception for cmd line parser
    	  	catch (ParseException e1) {
			e1.printStackTrace();
		}    
      }
 }
 