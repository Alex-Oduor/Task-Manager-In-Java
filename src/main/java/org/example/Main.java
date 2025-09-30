package org.example;

/*  TaskManager v.1

    *Guidelines  when using this Task Manager v.1

1.Tasks must be completed 24 hours after creation.
2.Tasks are to be initialized or created the day they are supposed to be done.


*/





import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.sql.*;


public class Main{

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        char choice;
        do{
            System.out.println("\t\t\t*** Check List ***");
            System.out.println("\t\t\t\t1. View");
            System.out.println("\t\t\t\t2. Add");
            System.out.println("\t\t\t\t3. Complete");
            System.out.println("\t\t\t\t4. Exit");

            System.out.print("\t\t\tchoose one option: ");
            int option =scanner.nextInt();

            switch (option){
                case 1:
                    viewTask();
                    break;
                case 2:
                    addTask();
                    break;
                case 3:
                    completeTask();
                    break;
                case 4:
                    System.exit(0);
                    break;
                default:
                    System.out.print("\t\t\tInvalid Input please try again!");
            }

            System.out.print("\t\t\tWould you like to continue?[Y/N]");
            choice =scanner.next().charAt(0);
        }while(choice=='Y' || choice =='y');

    }

    /* Method to view tasks  */
    private static void viewTask(){
        Scanner scanner= new Scanner(System.in);

        try{
            Connection connection = DriverManager.getConnection("jdbc:sqlite:task.db");
            Statement statement= connection.createStatement();


            System.out.println("\t\t\t*****View Tasks*****");
            System.out.println("\t\t\t\t1.All Tasks");
            System.out.println("\t\t\t\t2.Today's Tasks");
            System.out.println("\t\t\t\t2.Exit");
            System.out.print("\t\t\tChoose one:");
            int viewChoice = scanner.nextInt();

            if(viewChoice==1){
                String sql ="SELECT *FROM Tasks;";


                ResultSet output= statement.executeQuery(sql);
                System.out.println("\t\t\t******All TASKS******\n");

                while (output.next()) {
                    String task = output.getString("name");
                    boolean status = output.getBoolean("status");
                    String creationDate = output.getString("creationDate");
                    String deadlineDate = output.getString("deadlineDate");

                    String state;
                    if(status){
                        state="Completed";
                    }else{
                        state="Incomplete";
                    }


                    System.out.println("\t\t\tTask: " + task+"("+state+")\t\t\t created on "+creationDate+" ...to be completed by "+deadlineDate);


                }
                System.out.print("\n");


            }else if(viewChoice==2){
                LocalDate today = LocalDate.now();
                String todayDate =today.toString();

                String sql ="SELECT *FROM Tasks WHERE creationDate='"+todayDate+"'";


                ResultSet output= statement.executeQuery(sql);
                System.out.println("\t\t\t******Today's TASKS******\n");


                if(!output.next()){
                    System.out.println("\t\t\tNo tasks created today!\n");

                }else{

                   do {
                        String task = output.getString("name");
                        boolean status = output.getBoolean("status");
                        String creationDate = output.getString("creationDate");
                        String deadlineDate = output.getString("deadlineDate");

                        String state;
                        if (status) {
                            state = "Completed";
                        } else {
                            state = "Incomplete";
                        }
                        System.out.println("\t\t\tTask: " + task + "(" + state + ")\t\t\t created on " + creationDate + " ...to be completed by " + deadlineDate);

                    } while (output.next());
                    System.out.print("\n");


                }

            }else if(viewChoice==3){
                System.exit(0);
            }

        }catch (SQLException SE){
            System.err.println("Can't connect to database!!");
        }

    }
    /* method to complete a task */
    private static void completeTask(){
        try(
                Connection conn = DriverManager.getConnection("jdbc:sqlite:task.db")
        ){

            Statement statement = conn.createStatement();
            String sql = "SELECT *FROM Tasks where status==0;";
            ResultSet incompletedTasks = statement.executeQuery(sql);


            while(incompletedTasks.next()){
                int id= incompletedTasks.getInt("id");
                String task = incompletedTasks.getString("name");

                System.out.println("Id: "+id+"\tTask: "+task);
            }
            System.out.print("Which task have you finished?");
            Scanner scanner = new Scanner(System.in);
            int taskToFinish = scanner.nextInt();
            
            boolean completed= true;

            String sql2 = ("UPDATE Tasks set status ="+completed+" WHERE id=="+taskToFinish+";");
            Statement stmt = conn.createStatement();
            int i = stmt.executeUpdate(sql2);
            System.out.println(i+" Task updated");


        }catch (SQLException se){
            System.err.println("Can't connect to database");
        }


    }


/*method to add task */
    private static void addTask(){

        Scanner input = new Scanner(System.in);
        System.out.println("*** Add Task ***");
        System.out.println("Enter task");

        String newTask = input.nextLine();

        LocalDate today = LocalDate.now();
        String taskCreationDate = today.toString();
        String deadlineDate = setDeadline(today);

        task task1= new task(newTask,taskCreationDate,deadlineDate);
        boolean complete= task1.getState();


        saveTask(newTask ,taskCreationDate, deadlineDate , complete);
    }

    private static String setDeadline(LocalDate today) {
        Month month = today.getMonth();
        int monthValue =month.getValue();

        int year = today.getYear();
        int day = today.getDayOfMonth() +1;

        //check if its end of the month
        int lengthOfMonth = today.lengthOfMonth();
        String deadlineDate;
        if(day>lengthOfMonth){
            day=1;
            monthValue +=1;
            LocalDate deadline = LocalDate.of(year,monthValue,day);
            deadlineDate = deadline.toString();
        }else{
            LocalDate deadline = LocalDate.of(year,month,day);
            deadlineDate = deadline.toString();
        }
        return deadlineDate;
    }


    //method to save a task
    private static void saveTask(String Task , String creationDate , String deadline , boolean status){

        try
        {
            File database = new File("task.db");
            if(database.exists()){
                Connection conn = DriverManager.getConnection("jdbc:sqlite:task.db");
                String sql = "INSERT INTO Tasks(name,creationDate ,status ,deadlineDate) VALUES(?,?,?,?)";

                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1,Task);
                preparedStatement.setString(2, creationDate);
                preparedStatement.setBoolean(3, status);
                preparedStatement.setString(4, deadline);

                int rowsInserted =preparedStatement.executeUpdate();
                System.out.println("Task added, inserted "+rowsInserted+" rows");

            }else{
                Connection conn =DriverManager.getConnection("jdbc:sqlite:task.db");
                Statement statement = conn.createStatement();

                String sql = """
                        CREATE TABLE Tasks (
                        \t"id"\tINTEGER NOT NULL,
                        \t"name"\tTEXT NOT NULL,
                        \t"creationDate"\tTEXT,
                        \t"status"\tTEXT,
                        \t"deadlineDate"\tTEXT,
                        \tPRIMARY KEY("id")
                        )""";

                statement.executeUpdate(sql);

                String sql2 = "INSERT INTO Tasks(name,creationDate ,status ,deadlineDate) VALUES(?,?,?,?)";

                PreparedStatement preparedStatement = conn.prepareStatement(sql2);
                preparedStatement.setString(1,Task);
                preparedStatement.setString(2, creationDate);
                preparedStatement.setBoolean(3, status);
                preparedStatement.setString(4, deadline);

                int rowsInserted =preparedStatement.executeUpdate();
                System.out.println("Task added, inserted "+rowsInserted+" rows");

            }

        }catch (SQLException se){
            System.err.println("Can't connect to database..");
            }

    }

}