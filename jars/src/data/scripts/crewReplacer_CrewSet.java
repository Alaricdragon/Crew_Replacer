package data.scripts;

import java.util.ArrayList;

public class crewReplacer_CrewSet {
    protected static final String className = "crewReplacer_CrewSet";
    public String name;
    public ArrayList<crewReplacer_Crew> Crews = new ArrayList<>();
    public ArrayList<String> CrewSets = new ArrayList<>();
    public crewReplacer_CrewSet(){

    }

    public boolean hasCrew(String crew){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + CrewReplacer_StringHelper.getLogString(className,"hasCrew",0),this);
        CrewReplacer_Log.push();
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew)){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"hasCrew",1,crew),this);
                CrewReplacer_Log.pop();
                return true;
            }
        }
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"hasCrew",2,crew),this);
        CrewReplacer_Log.pop();
        return false;
    }
    public crewReplacer_Crew getCrew(String crew){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + CrewReplacer_StringHelper.getLogString(className,"getCrew",0),this);
        CrewReplacer_Log.push();
        boolean out = true;
        crewReplacer_Crew output = null;
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew)){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrew",1,crew),this);
                output = Crews.get(a);
                out = false;
                break;
            }
        }
        if(out){
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrew",2,crew),this);
            output = new crewReplacer_Crew();
            output.name = crew;
            Crews.add(output);
        }
        CrewReplacer_Log.pop();
        return output;
    }
    public boolean removeCrew(String crew){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + CrewReplacer_StringHelper.getLogString(className,"removeCrew",0),this);
        CrewReplacer_Log.push();
        //boolean output = false;
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew)){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"removeCrew",1,Crews.get(a).name),this);
                Crews.remove(a);
                //organizePriority();//for now. will have to change later?
                CrewReplacer_Log.pop();
                return true;
            }
        }
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"removeCrew",2,crew),this);
        CrewReplacer_Log.pop();
        return false;
    }
    public boolean addCrew(crewReplacer_Crew crew){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + CrewReplacer_StringHelper.getLogString(className,"addCrew",0),this);
        CrewReplacer_Log.push();
        boolean output = true;
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew.name)){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addCrew",1,Crews.get(a).name),this);
                output = false;
                mergeCrew(Crews.get(a),crew.crewPower,crew.crewDefence,crew.crewPriority/*,crew.maxLosePercent,crew.minLosePercent,crew.NormalLossRules*/);
                break;
            }
        }
        if(output){
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addCrew",2,crew.name),this);
            Crews.add(crew);
        }
        CrewReplacer_Log.pop();
        return output;
    }
    public boolean addNewCrew(String crew,float crewPower,float crewPriority){
        return addNewCrew(crew,crewPower,crewPower,crewPriority);
    }
    public boolean addNewCrew(String crew,float crewPower,float crewDefence,float crewPriority){
        boolean output = true;
        crewReplacer_Crew temp = new crewReplacer_Crew();
        CrewReplacer_Log.loging(getIntoCrewSetLog() + CrewReplacer_StringHelper.getLogString(className,"addNewCrew",0,crew,""+crewPower,""+crewPriority),this);
        CrewReplacer_Log.push();
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew)){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addNewCrew",1,Crews.get(a).name),this);
                output = false;
                temp = Crews.get(a);
                break;
            }
        }
        if(output){
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addNewCrew",2,crew),this);
            temp.name = crew;
            Crews.add(temp);
        }
        mergeCrew(temp,crewPower,crewDefence,crewPriority/*,crewMaxLosePercent,crewMinLosePercent,crewNormalLoseRules*/);
        CrewReplacer_Log.pop();
        return output;
    }
    private void mergeCrew(crewReplacer_Crew crew,float crewPower,float crewDefence,float crewPriority){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + CrewReplacer_StringHelper.getLogString(className,"mergeCrew",0,crew.name,""+crewPower,""+crewDefence,""+crewPriority),this);
        crew.crewPower = crewPower;
        crew.crewDefence = crewDefence;
        crew.crewPriority = crewPriority;
        //crew.maxLosePercent = crewMaxLosePercent;
        //crew.maxLosePercent = crewMinLosePercent;
        //crew.NormalLossRules = crewNormalLoseRules;
    }


    public boolean addCrewSet(String CrewSet){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + CrewReplacer_StringHelper.getLogString(className,"addCrewSet",0),this);
        CrewReplacer_Log.push();
        for(String a : CrewSets){
            if(a.equals(CrewSet)){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addCrewSet",1,CrewSet),this);
                CrewReplacer_Log.pop();
                return false;
            }
        }
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addCrewSet",2,CrewSet),this);
        CrewSets.add(CrewSet);
        CrewReplacer_Log.pop();
        return true;
    }
    public boolean removeCrewSet(String CrewSet){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + CrewReplacer_StringHelper.getLogString(className,"removeCrewSet",0),this);
        CrewReplacer_Log.push();
        for(String a : CrewSets){
            if(a.equals(CrewSet)){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"removeCrewSet",1,CrewSet),this);
                CrewReplacer_Log.pop();
                return true;
            }
        }
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"removeCrewSet",2,CrewSet),this);
        CrewSets.add(CrewSet);
        CrewReplacer_Log.pop();
        return false;
    }
    public ArrayList<crewReplacer_Crew> getAllLinkedCrews(){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + CrewReplacer_StringHelper.getLogString(className,"getAllLinkedCrews",0),this);
        CrewReplacer_Log.push();
        ArrayList<crewReplacer_Crew> output = new ArrayList<>();
        for(crewReplacer_Crew b : Crews){
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getAllLinkedCrews",1,b.name),this);
            output.add(b);
        }
        for(String a : CrewSets){
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getAllLinkedCrews",2,a),this);
            CrewReplacer_Log.push();
            for(crewReplacer_Crew b : crewReplacer_Main.getCrewSet(a).getAllLinkedCrews()){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getAllLinkedCrews",3,b.name),this);
                output.add(b);
            }
            CrewReplacer_Log.pop();
        }
        CrewReplacer_Log.pop();
        return output;
    }

    private String getIntoCrewSetLog(){
        return CrewReplacer_StringHelper.getLogString(className,"getIntoCrewSetLog",0,name);
    }
}
