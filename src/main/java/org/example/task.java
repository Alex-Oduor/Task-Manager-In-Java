package org.example;
import java.time.LocalDate;
import java.util.Date;

public class task {
    String task;
    String dateCreated;
    String deadline;

    //default variable
    boolean complete=false;



    public task(String a , String b, String c){
        this.task=a;
        this.dateCreated=b;
        this.deadline=c;
    }

    public boolean getState() {
        return complete;
    }

    public String getTask() {
        return task;
    }

    public String showTask(){
        if (!complete){
            return (task + "    **to be completed by  " + deadline + " **");
        }else{
            return (task + "    **COMPLETED**");
        }
    }

    public boolean taskState(){
        return complete;
    }

}

