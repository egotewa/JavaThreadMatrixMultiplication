import java.util.Calendar;

class MatrixProduct extends Thread {
      private int[][] A;
      private int[][] B;
      private int[][] C;
      private int rig,col;
      private int dim;

      public MatrixProduct(int[][] A,int[][] B,int[][] C,int rig, int col,int dim_com)
      {
         this.A=A;    
         this.B=B;
         this.C=C;
         this.rig=rig;    
         this.col=col; 
         this.dim=dim_com;     
      }

     public void run()
     {
         for(int i=0;i<dim;i++){
               C[rig][col]+=A[rig][i]*B[i][col];        
         }      
          System.out.println("Thread "+rig+","+col+" complete.");        
     }          
 }

 public class MatrixMultiplication {
       public static void main(String[] args)
      {      
          /*Scanner In=new    Scanner(System.in); 

          System.out.print("Row of Matrix A: ");     
          int rA=In.nextInt();
          System.out.print("Column of Matrix A: ");
          int cA=In.nextInt();
          System.out.print("Row of Matrix B: ");     
          int rB=In.nextInt();
          System.out.print("Column of Matrix B: ");
          int cB=In.nextInt();
          System.out.println();*/
    	   
    	  if (args.length != 3) {
   			System.out.println("Please enter all 3 parameters and try again.");
   			System.exit(1);
   		}
    	  	//creating Calendar instance 
    	  	Calendar cal = Calendar.getInstance();
    	  	
    	  	long t1=cal.getTimeInMillis();
    	  
    	  	//getting matrices dimensions
    	  	int numberOfRowsInMatrixA = new Integer(args[0]);
  			int numberOfColumnsInMatrixA = new Integer(args[1]);
  			int numberOfColumnsInMatrixB= new Integer(args[2]);
  			
  			System.out.println("Result matrix will be with dimensions:"+ numberOfRowsInMatrixA+ " x " + numberOfColumnsInMatrixB);
	       System.out.println();
	       
	       //creating arrays
	       int[][] A=new int[numberOfRowsInMatrixA][numberOfColumnsInMatrixA];
	       int[][] B=new int[numberOfColumnsInMatrixA][numberOfColumnsInMatrixB];
	       int[][] C=new int[numberOfRowsInMatrixA][numberOfColumnsInMatrixB];
	       
	       //creating threads
	       MatrixProduct[][] myThreads= new MatrixProduct[numberOfRowsInMatrixA][numberOfColumnsInMatrixB];

	       
       System.out.println("Generating matrix A...");
        for(int i=0; i<numberOfRowsInMatrixA; i++)
         {
          for(int j=0;j<numberOfColumnsInMatrixA;j++)
          {
        	  //generating random numbers*number of rows in matrix A
              A[i][j]=(int) (Math.random() * A[i].length);
          }
         }      
        System.out.println("Matrix A:");
        for(int i=0; i<numberOfRowsInMatrixA; i++)
        {
         for(int j=0;j<numberOfColumnsInMatrixA;j++)
         {
        	 System.out.print(A[i][j]+ " ");
         }
         System.out.println();
        }      
        
         System.out.println();    
         System.out.println("Generating matrix B...");
         System.out.println();
         
          for(int i=0; i<numberOfColumnsInMatrixA ;i++)
          {
           for(int j=0; j<numberOfColumnsInMatrixB; j++)
            {
        	   //generating random numbers*number of columns in matrix B
	            B[i][j]=(int) (Math.random() * B.length);
            }        
          }
          System.out.println("Matrix B:");
          for(int i=0; i<numberOfColumnsInMatrixA; i++)
          {
           for(int j=0;j<numberOfColumnsInMatrixB;j++)
           {
          	 System.out.print(B[i][j]+ " ");
           }
           System.out.println();
          }   
          
          
          System.out.println();

         //Threads creation and starting
        for(int i=0; i<numberOfRowsInMatrixA; i++)
        {
         for(int j=0; j<numberOfColumnsInMatrixB; j++)
          {
            myThreads[i][j]=new MatrixProduct(A,B,C,i,j,numberOfColumnsInMatrixA);
            myThreads[i][j].start();
          }
        }

        for(int i=0; i<numberOfRowsInMatrixA; i++)
        {
            for(int j=0; j<numberOfColumnsInMatrixB; j++)
            {
                try{
                    myThreads[i][j].join();
                }
            catch(InterruptedException e){}
            }
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
        }  
        
        Calendar cal2= Calendar.getInstance();
        long t2=cal2.getTimeInMillis();
        System.out.println("Execution time: " + (t2-t1));
      }   
       
}