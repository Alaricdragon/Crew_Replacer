package data.scripts;

import java.util.ArrayList;

public class crewReplacer_Main {
    private static final String className = "crewReplacer_Main";
    static private ArrayList<crewReplacer_Job> Jobs = new ArrayList<>();
    static private ArrayList<crewReplacer_CrewSet> CrewSets = new ArrayList<>();
    //ArrayList<crewReplacer_Crew> Crews = new ArrayList<>();

    static public crewReplacer_Job getJob(String job){
        crewReplacer_Job output = null;
        boolean temp = true;
        for(int a = 0; a < Jobs.size(); a++){
            if(Jobs.get(a).name.equals(job)){//
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getJob",0,job),new crewReplacer_Main());
                output = Jobs.get(a);
                temp = false;
                break;
            }
        }
        if(temp){//output == null){
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getJob",1,job),new crewReplacer_Main());
            output = addJob(job);
        }
        return output;
    }
    static public boolean removeJob(String job){
        boolean output = false;
        for(int a = 0; a < Jobs.size(); a++){
            if(Jobs.get(a).name.equals(job)){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"removeJob",0,job),new crewReplacer_Main());
                Jobs.remove(a);
                output = true;
                break;
            }
        }
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"removeJob",1,job), new crewReplacer_Main());
        return output;
    }
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
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addOrMergeJob",0,job.name),new crewReplacer_Main());
            /*if(job.name.equals("")){
                throw new ClassCastException("jobs must have names!");
            }*/
            Jobs.add(job);
        }else{
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addOrMergeJob",1,job.name), new crewReplacer_Main());
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
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"organizePriority",0), new crewReplacer_Main());
        CrewReplacer_Log.push();
        for(int a = 0; a < Jobs.size(); a++){
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"organizePriority",1,Jobs.get(a).name),new crewReplacer_Main());
            Jobs.get(a).applyCrewSets();
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"organizePriority",2,Jobs.get(a).name),new crewReplacer_Main());
            Jobs.get(a).organizePriority();
        }
        CrewReplacer_Log.pop();
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


    static public crewReplacer_CrewSet getCrewSet(String crewSet){
        crewReplacer_CrewSet output = null;
        boolean temp = true;
        for(int a = 0; a < CrewSets.size(); a++){
            if(CrewSets.get(a).name.equals(crewSet)){//
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewSet",0,crewSet),new crewReplacer_Main());
                output = CrewSets.get(a);
                temp = false;
                break;
            }
        }
        if(temp){//output == null){
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewSet",1,crewSet),new crewReplacer_Main());
            output = addCrewSet(crewSet);
        }
        return output;
    }
    static public boolean removeCrewSet(String crewSet){
        boolean output = false;
        for(int a = 0; a < CrewSets.size(); a++){
            if(CrewSets.get(a).name.equals(crewSet)){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"removeCrewSet",0,crewSet),new crewReplacer_Main());
                CrewSets.remove(a);
                output = true;
                break;
            }
        }
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"removeCrewSet",1,crewSet), new crewReplacer_Main());
        return output;
    }
    static public boolean addOrMergeCrewSet(crewReplacer_CrewSet crewSet){
        boolean output = true;
        int temp = 0;
        for(int a = 0; a < CrewSets.size(); a++){
            if(CrewSets.get(a).name.equals(crewSet.name)){
                temp = a;
                output = false;
                break;
            }
        }
        if(output){
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addOrMergeCrewSet",0,crewSet.name),new crewReplacer_Main());
            /*if(crew.name.equals("")){
                throw new ClassCastException("crews must have names!");
            }*/
            CrewSets.add(crewSet);
        }else{
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addOrMergeCrewSet",1,crewSet.name), new crewReplacer_Main());
            crewReplacer_CrewSet tempcrew = CrewSets.get(temp);
            CrewSets.remove(temp);
            for(int a = 0; a < tempcrew.Crews.size(); a++) {
                crewSet.addCrew(tempcrew.Crews.get(a));
            }
            CrewSets.add(crewSet);
        }
        return output;
    }
    static private crewReplacer_CrewSet addCrewSet(String name){
        crewReplacer_CrewSet temp = new crewReplacer_CrewSet();
        //temp.ID = Jobs.get(Jobs.size() - 1).ID + 1;
        //although it pains me, i will let people add jobs that have no names. they will have to have there own way to accses said job though.
        /*if(name.equals("")){
            throw new ClassCastException("jobs must have names!");
        }*/
        temp.name = name;
        CrewSets.add(temp);
        return temp;
    }
}

