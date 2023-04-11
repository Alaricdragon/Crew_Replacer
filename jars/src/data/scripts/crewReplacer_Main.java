package data.scripts;

import java.util.ArrayList;

public class crewReplacer_Main {
    static private ArrayList<crewReplacer_Job> Jobs = new ArrayList<>();
    static private ArrayList<crewReplacer_crewSet> CrewSets = new ArrayList<>();
    //ArrayList<crewReplacer_Crew> Crews = new ArrayList<>();

    static public crewReplacer_Job getJob(String job){
        crewReplacer_Job output = null;
        boolean temp = true;
        for(int a = 0; a < Jobs.size(); a++){
            if(Jobs.get(a).name.equals(job)){//
                CrewReplacer_Log.loging("getJob found matching job name. getting job named: " + job,new crewReplacer_Main());
                output = Jobs.get(a);
                temp = false;
                break;
            }
        }
        if(temp){//output == null){
            CrewReplacer_Log.loging("getJob job not formed yet. creating new job named: " + job,new crewReplacer_Main());
            output = addJob(job);
        }
        return output;
    }
    static public boolean removeJob(String job){
        boolean output = false;
        for(int a = 0; a < Jobs.size(); a++){
            if(Jobs.get(a).name.equals(job)){
                CrewReplacer_Log.loging("removeJob sucsesfull. removeing job " + job,new crewReplacer_Main());
                Jobs.remove(a);
                output = true;
                break;
            }
        }
        CrewReplacer_Log.loging("remove job failed. no job name: " + job, new crewReplacer_Main());
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
            CrewReplacer_Log.loging("addOrMergeJob didnt find any job by the name: " + job + ". creating new job.",new crewReplacer_Main());
            /*if(job.name.equals("")){
                throw new ClassCastException("jobs must have names!");
            }*/
            Jobs.add(job);
        }else{
            CrewReplacer_Log.loging("addOrMergeJob found a matching job named: " + job + ". moveing data to new class...", new crewReplacer_Main());
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
        CrewReplacer_Log.loging("organizing Priority...", new crewReplacer_Main());
        CrewReplacer_Log.push();
        for(int a = 0; a < Jobs.size(); a++){
            CrewReplacer_Log.loging("adding crews from relevant jobSets to job named: " + Jobs.get(a).name,new crewReplacer_Main());
            Jobs.get(a).applyCrewSets();
            CrewReplacer_Log.loging("organizing job named: " + Jobs.get(a).name,new crewReplacer_Main());
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


    static public crewReplacer_crewSet getCrewSet(String crewSet){
        crewReplacer_crewSet output = null;
        boolean temp = true;
        for(int a = 0; a < CrewSets.size(); a++){
            if(CrewSets.get(a).name.equals(crewSet)){//
                CrewReplacer_Log.loging("getCrewSet found matching crewSet name. getting crewSet named: " + crewSet,new crewReplacer_Main());
                output = CrewSets.get(a);
                temp = false;
                break;
            }
        }
        if(temp){//output == null){
            CrewReplacer_Log.loging("getCrewSet crewSet not formed yet. creating new crewSet named: " + crewSet,new crewReplacer_Main());
            output = addCrewSet(crewSet);
        }
        return output;
    }
    static public boolean removeCrewSet(String crewSet){
        boolean output = false;
        for(int a = 0; a < CrewSets.size(); a++){
            if(CrewSets.get(a).name.equals(crewSet)){
                CrewReplacer_Log.loging("remove CrewSet sucsesfull. removeing crewSet " + crewSet,new crewReplacer_Main());
                CrewSets.remove(a);
                output = true;
                break;
            }
        }
        CrewReplacer_Log.loging("remove crewSet failed. no crewSet name: " + crewSet, new crewReplacer_Main());
        return output;
    }
    static public boolean addOrMergeCrewSet(crewReplacer_crewSet crewSet){
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
            CrewReplacer_Log.loging("addOrMergeCrewSet didnt find any crewSet by the name: " + crewSet + ". creating new crewSet.",new crewReplacer_Main());
            /*if(crew.name.equals("")){
                throw new ClassCastException("crews must have names!");
            }*/
            CrewSets.add(crewSet);
        }else{
            CrewReplacer_Log.loging("addOrMergeCrewSet found a matching crewSet named: " + crewSet + ". moveing data to new class...", new crewReplacer_Main());
            crewReplacer_crewSet tempcrew = CrewSets.get(temp);
            CrewSets.remove(temp);
            for(int a = 0; a < tempcrew.Crews.size(); a++) {
                crewSet.addCrew(tempcrew.Crews.get(a));
            }
            CrewSets.add(crewSet);
        }
        return output;
    }
    static private crewReplacer_crewSet addCrewSet(String name){
        crewReplacer_crewSet temp = new crewReplacer_crewSet();
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

