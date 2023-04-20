package data.scripts;

import java.util.ArrayList;

public class crewReplacer_CrewSet {
    public String name;
    public ArrayList<crewReplacer_Crew> Crews = new ArrayList<>();
    public ArrayList<String> CrewSets = new ArrayList<>();
    public crewReplacer_CrewSet(){

    }

    public boolean hasCrew(String crew){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + "running hasCrew",this);
        CrewReplacer_Log.push();
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew)){
                CrewReplacer_Log.loging("found crew with name: " + crew,this);
                CrewReplacer_Log.pop();
                return true;
            }
        }
        CrewReplacer_Log.loging("no crew found with the name: " + crew,this);
        CrewReplacer_Log.pop();
        return false;
    }
    public crewReplacer_Crew getCrew(String crew){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + "running getCrew",this);
        CrewReplacer_Log.push();
        boolean out = true;
        crewReplacer_Crew output = null;
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew)){
                CrewReplacer_Log.loging("crew found. returning crew named: " + crew,this);
                output = Crews.get(a);
                out = false;
                break;
            }
        }
        if(out){
            CrewReplacer_Log.loging("crew not found. creating a new crew named: " + crew,this);
            output = new crewReplacer_Crew();
            output.name = crew;
            Crews.add(output);
        }
        CrewReplacer_Log.pop();
        return output;
    }
    public boolean removeCrew(String crew){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + "running Remove",this);
        CrewReplacer_Log.push();
        //boolean output = false;
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew)){
                CrewReplacer_Log.loging("crew found. removing crew named: " + Crews.get(a).name,this);
                Crews.remove(a);
                //organizePriority();//for now. will have to change later?
                CrewReplacer_Log.pop();
                return true;
            }
        }
        CrewReplacer_Log.loging("no crew found. cannot remove crew named: " + crew,this);
        CrewReplacer_Log.pop();
        return false;
    }
    public boolean addCrew(crewReplacer_Crew crew){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + "running Add Crew",this);
        CrewReplacer_Log.push();
        boolean output = true;
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew.name)){
                CrewReplacer_Log.loging("crew found. merging crews named: " + Crews.get(a).name,this);
                output = false;
                mergeCrew(Crews.get(a),crew.crewPower,crew.crewDefence,crew.crewPriority/*,crew.maxLosePercent,crew.minLosePercent,crew.NormalLossRules*/);
                break;
            }
        }
        if(output){
            CrewReplacer_Log.loging("crew not found. creating new crew named: " + crew,this);
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
        CrewReplacer_Log.loging(getIntoCrewSetLog() + "trying to add new crew named: " + crew + " wtih a power & priority of: " + crewPower + " & " + crewPriority,this);
        CrewReplacer_Log.push();
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew)){
                CrewReplacer_Log.loging("crew found. preparing merge for crew new named: " + Crews.get(a).name,this);
                output = false;
                temp = Crews.get(a);
                break;
            }
        }
        if(output){
            CrewReplacer_Log.loging("no crew found. adding crew named: " + crew,this);
            temp.name = crew;
            Crews.add(temp);
        }
        mergeCrew(temp,crewPower,crewDefence,crewPriority/*,crewMaxLosePercent,crewMinLosePercent,crewNormalLoseRules*/);
        CrewReplacer_Log.pop();
        return output;
    }
    private void mergeCrew(crewReplacer_Crew crew,float crewPower,float crewDefence,float crewPriority){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + "running Merge Crew... setting stats to name: " + crew.name + ", power: " + crewPower + ", defence: " + crewDefence + ", priority: " + crewPriority,this);
        crew.crewPower = crewPower;
        crew.crewDefence = crewDefence;
        crew.crewPriority = crewPriority;
        //crew.maxLosePercent = crewMaxLosePercent;
        //crew.maxLosePercent = crewMinLosePercent;
        //crew.NormalLossRules = crewNormalLoseRules;
    }


    public boolean addCrewSet(String CrewSet){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + "running addCrewSet",this);
        CrewReplacer_Log.push();
        for(String a : CrewSets){
            if(a.equals(CrewSet)){
                CrewReplacer_Log.loging("CrewSet '" + CrewSet + "' already in CrewSets. cannot add the same crew set twice",this);
                CrewReplacer_Log.pop();
                return false;
            }
        }
        CrewReplacer_Log.loging("adding CrewSet '" + CrewSet + "' to CrewSets",this);
        CrewSets.add(CrewSet);
        CrewReplacer_Log.pop();
        return true;
    }
    public boolean removeCrewSet(String CrewSet){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + "running removeCrewSet",this);
        CrewReplacer_Log.push();
        for(String a : CrewSets){
            if(a.equals(CrewSet)){
                CrewReplacer_Log.loging("CrewSet '" + CrewSet + "' found in jobSets. removing.",this);
                CrewReplacer_Log.pop();
                return true;
            }
        }
        CrewReplacer_Log.loging("failed to find a CrewSet named '" + CrewSet + "' in CrewSets.",this);
        CrewSets.add(CrewSet);
        CrewReplacer_Log.pop();
        return false;
    }
    public ArrayList<crewReplacer_Crew> getAllLinkedCrews(){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + "running getAllLinedCrews",this);
        CrewReplacer_Log.push();
        ArrayList<crewReplacer_Crew> output = new ArrayList<>();
        for(crewReplacer_Crew b : Crews){
            CrewReplacer_Log.loging("adding crew to output named: " + b.name,this);
            output.add(b);
        }
        for(String a : CrewSets){
            CrewReplacer_Log.loging("adding crew sets crew to output named: " + a,this);
            CrewReplacer_Log.push();
            for(crewReplacer_Crew b : crewReplacer_Main.getCrewSet(a).getAllLinkedCrews()){
                CrewReplacer_Log.loging("adding crew to output named: " + b.name,this);
                output.add(b);
            }
            CrewReplacer_Log.pop();
        }
        CrewReplacer_Log.pop();
        return output;
    }

    private String getIntoCrewSetLog(){
        return "in job named '" + name + "' ";
    }
}
