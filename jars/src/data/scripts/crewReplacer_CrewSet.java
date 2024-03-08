package data.scripts;

import java.util.ArrayList;

public class crewReplacer_CrewSet {
    private static final int maxDepthForGathering = 50;
    protected static final String className = "crewReplacer_CrewSet";
    public String name;
    public ArrayList<CrewReplacer_BlackListCrew> BlackListCrews = new ArrayList<>();
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
        for(CrewReplacer_BlackListCrew a : BlackListCrews){
            if (a.crew.equals(crew.name)){
                if (a.loadPriority >= crew.crewLoadPriority){
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addCrew",4,""+a.loadPriority,""+crew.crewLoadPriority),this);
                    CrewReplacer_Log.pop();
                    return false;//?
                }else{
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addCrew",5,""+a.loadPriority,""+crew.crewLoadPriority),this);
                    //continue
                    break;
                }
            }
        }
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew.name)){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addCrew",1,Crews.get(a).name),this);
                if (Crews.get(a).crewLoadPriority > crew.crewLoadPriority){
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addCrew",2,Crews.get(a).name),this);
                    return false;
                }
                Crews.remove(a);
                Crews.add(a,crew);
                CrewReplacer_Log.push();
                //mergeCrew(Crews.get(a),crew.crewPower,crew.crewDefence,crew.crewPriority/*,crew.maxLosePercent,crew.minLosePercent,crew.NormalLossRules*/);
                return true;
            }
        }
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addCrew",3,crew.name),this);
        Crews.add(crew);
        CrewReplacer_Log.pop();
        return true;
    }
    public boolean addNewCrew(String crew,float crewPower,float crewPriority){
        return addNewCrew(crew,crewPower,crewPower,crewPriority,0);
    }
    public boolean addNewCrew(String crew,float crewPower,float crewPriority,float loadPriority){
        return addNewCrew(crew,crewPower,crewPower,crewPriority,loadPriority);
    }
    public boolean addNewCrew(String crew,float crewPower,float crewDefence,float crewPriority,float loadPriority){
        boolean output = true;
        crewReplacer_Crew temp = new crewReplacer_Crew();
        CrewReplacer_Log.loging(getIntoCrewSetLog() + CrewReplacer_StringHelper.getLogString(className,"addNewCrew",0,crew,""+crewPower,""+crewPriority,""+loadPriority),this);
        CrewReplacer_Log.push();
        crewReplacer_Crew a = new crewReplacer_Crew();
        a.name = crew;
        a.crewPower = crewPower;
        a.crewDefence = crewDefence;
        a.crewPriority = crewPriority;
        a.crewLoadPriority = loadPriority;
        output = addCrew(a);
        /*for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew)){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addNewCrew",1,Crews.get(a).name),this);
                if (loadPriority < Crews.get(a).crewLoadPriority){
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addNewCrew",2,Crews.get(a).name),this);
                    return false;
                }
                output = false;
                temp = Crews.get(a);
                break;
            }
        }
        if(output){
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addNewCrew",3,crew),this);

            temp.name = crew;
            Crews.add(temp);
        }
        mergeCrew(temp,crewPower,crewDefence,crewPriority,loadPriority);*/
        CrewReplacer_Log.pop();
        return output;
    }



    public boolean addBlackListCrew(String crew, float loadPriority) {
        CrewReplacer_Log.loging(getIntoCrewSetLog()+CrewReplacer_StringHelper.getLogString(className,"addBlackListCrew",0,crew,""+loadPriority),this);
        CrewReplacer_Log.push();
        for (int c = 0; c < Crews.size(); c++){
            crewReplacer_Crew a = Crews.get(c);
            if (a.name.equals(crew)){
                if(a.crewLoadPriority <= loadPriority){
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addBlackListCrew",1,""+a.crewLoadPriority,""+loadPriority),this);
                    boolean temp = addBlackListCrew(new CrewReplacer_BlackListCrew(crew,loadPriority));
                    CrewReplacer_Log.pop();
                    return temp;
                }else{
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addBlackListCrew",2,""+a.crewLoadPriority,""+loadPriority),this);
                    CrewReplacer_Log.pop();
                    return false;
                }
            }
        }
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addBlackListCrew",3),this);
        boolean temp = addBlackListCrew(new CrewReplacer_BlackListCrew(crew,loadPriority));
        CrewReplacer_Log.pop();
        return temp;
    }
    protected boolean addBlackListCrew(CrewReplacer_BlackListCrew EC){
        CrewReplacer_Log.loging(getIntoCrewSetLog() + CrewReplacer_StringHelper.getLogString(className,"addBlackListCrew",4,EC.crew,""+EC.loadPriority),this);
        CrewReplacer_Log.push();
        for (int b = 0; b < BlackListCrews.size(); b++){
            if (BlackListCrews.get(b).loadPriority <= EC.loadPriority){
                BlackListCrews.remove(b);
                BlackListCrews.add(EC);
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addBlackListCrew",5,""+EC.loadPriority,""+ BlackListCrews.get(b).loadPriority),this);
                this.removeCrew(EC.crew);
                CrewReplacer_Log.pop();
                return true;
            }else{
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addBlackListCrew",6,""+EC.loadPriority,""+ BlackListCrews.get(b).loadPriority),this);
                CrewReplacer_Log.pop();
                return false;
            }
        }
        BlackListCrews.add(EC);
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addBlackListCrew",7),this);
        this.removeCrew(EC.crew);
        CrewReplacer_Log.pop();
        return true;
    }
    public CrewReplacer_BlackListCrew getBlackListCrewIfExists(String crew){
        CrewReplacer_Log.loging(getIntoCrewSetLog()+CrewReplacer_StringHelper.getLogString(className,"getBlackListCrewIfExists",0,crew),this);
        CrewReplacer_Log.push();
        for(int a = 0; a < BlackListCrews.size(); a++){
            if (BlackListCrews.get(a).crew.equals(crew)) {
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getBlackListCrewIfExists",1),this);
                CrewReplacer_Log.pop();
                return BlackListCrews.get(a);
            }
        }
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getBlackListCrewIfExists",2),this);
        CrewReplacer_Log.pop();
        return null;
    }
    public boolean removeBlackListCrew(String crew){
        CrewReplacer_Log.loging(getIntoCrewSetLog()+CrewReplacer_StringHelper.getLogString(className,"removeExcludeCrew",0,crew),this);
        CrewReplacer_Log.push();
        for(int a = 0; a < BlackListCrews.size(); a++){
            if (BlackListCrews.get(a).crew.equals(crew)) {
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"removeExcludeCrew",1),this);
                CrewReplacer_Log.pop();
                BlackListCrews.remove(a);
                return true;
            }
        }
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"removeExcludeCrew",2),this);
        CrewReplacer_Log.pop();
        return false;
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

    public crewReplacer_CrewSet getAllLinkedCrewsSets() {
        return getAllLinkedCrewsSets(maxDepthForGathering);
    }
    public crewReplacer_CrewSet getAllLinkedCrewsSets(int maxDepthForGathering){
        if (maxDepthForGathering <= 0) return new crewReplacer_CrewSet();
        maxDepthForGathering--;
        CrewReplacer_Log.loging(getIntoCrewSetLog() + CrewReplacer_StringHelper.getLogString(className,"getAllLinkedCrewsSets",0,""+maxDepthForGathering),this);
        CrewReplacer_Log.push();
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getAllLinkedCrewsSets",1),this);
        CrewReplacer_Log.push();
        crewReplacer_CrewSet output = new crewReplacer_CrewSet();
        for (CrewReplacer_BlackListCrew a : this.BlackListCrews){
            output.addBlackListCrew(a.crew,a.loadPriority);
        }
        CrewReplacer_Log.pop();
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getAllLinkedCrewsSets",2),this);
        CrewReplacer_Log.push();
        for (crewReplacer_Crew a : this.Crews){
            output.addCrew(a);
        }
        CrewReplacer_Log.pop();
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getAllLinkedCrewsSets",3),this);
        CrewReplacer_Log.push();
        for (String sets : this.CrewSets) {
            crewReplacer_CrewSet temp = crewReplacer_Main.getCrewSet(sets).getAllLinkedCrewsSets(maxDepthForGathering);
            for (CrewReplacer_BlackListCrew a : temp.BlackListCrews){
                output.addBlackListCrew(a.crew,a.loadPriority);
            }
            for (crewReplacer_Crew a : temp.Crews){
                output.addCrew(a);
            }
        }
        CrewReplacer_Log.pop();
        return output;
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
