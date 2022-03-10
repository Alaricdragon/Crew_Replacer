package data.scripts;

import java.util.ArrayList;

public class crewReplacer_Main {
    static ArrayList<crewReplacer_Job> Jobs = new ArrayList<>();
    //ArrayList<crewReplacer_Crew> Crews = new ArrayList<>();

    static public crewReplacer_Job getJob(String job){
        crewReplacer_Job output = null;
        boolean temp = true;
        for(int a = 0; a < Jobs.size(); a++){
            if(Jobs.get(a).name.equals(job)){
                output = Jobs.get(a);
                temp = false;
                break;
            }
        }
        if(temp){//output == null){
            output = addJob(job);
        }
        return output;
    }
    static public boolean removeJob(String job){
        boolean output = false;
        for(int a = 0; a < Jobs.size(); a++){
            if(Jobs.get(a).name.equals(job)){
                Jobs.remove(a);
                output = true;
                break;
            }
        }
        return output;
    }
    /*static public boolean addNewJob(String job){
        boolean output = true;
        for(int a = 0; a < Jobs.size(); a++){
            if(Jobs.get(a).name.equals(job)){
                output = false;
                break;
            }
        }
        if(output){
            addJob(job);
        }
        return output;
    }*/
    static public boolean addOrMergeJob(crewReplacer_Job job){
        boolean output = true;
        int temp = 0;
        for(int a = 0; a < Jobs.size(); a++){
            if(Jobs.get(a).name.equals(job.name)){
                temp = a;
                output = false;
                break;
            }
        }
        if(output){
            /*if(job.name.equals("")){
                throw new ClassCastException("jobs must have names!");
            }*/
            Jobs.add(job);
        }else{
            crewReplacer_Job tempjob = Jobs.get(temp);
            Jobs.remove(temp);
            for(int a = 0; a < tempjob.Crews.size(); a++) {
                job.addCrew(tempjob.Crews.get(a));
            }
            Jobs.add(job);
        }
        return output;
    }

    static public void organizePriority(){
        for(int a = 0; a < Jobs.size(); a++){
            Jobs.get(a).organizePriority();
        }
    }
    static private crewReplacer_Job addJob(String name){
        crewReplacer_Job temp = new crewReplacer_Job();
        //temp.ID = Jobs.get(Jobs.size() - 1).ID + 1;
        //although it pains me, i will let people add jobs that have no names. they will have to have there own way to accses said job though.
        /*if(name.equals("")){
            throw new ClassCastException("jobs must have names!");
        }*/
        temp.name = name;
        Jobs.add(temp);
        return temp;
    }
}
