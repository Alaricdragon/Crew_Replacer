package data.scripts;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import data.scripts.crews.CrewReplacer_BlankCrew;

import java.util.ArrayList;

public class crewReplacer_Job {
    protected static final String className = "crewReplacer_Job";
    /*
        requirements:
        (done)way to display and apply crew lost
        -when doing a job
            -for example: the crew lost salvage module only triggers if any crew were lost. might want to trigger always for min loss percent factors.
        -when applying other loss factors.
        -both

        (done?)way to display crew available
        -i don't want to rebuild the crew display 40 times thanks
     */
    public static final String CARGO_CARGO = "cargo", CARGO_CREW = "crew", CARGO_FUEL ="fuel";
    public String name = "";
    public float loadPriority = 0;
    public ArrayList<CrewReplacer_BlackListCrew> BlackListCrews = new ArrayList<>();
    public ArrayList<ArrayList<Integer>> crewPriority;//organized greatest to lowest.
    public ArrayList<crewReplacer_Crew> Crews = new ArrayList<crewReplacer_Crew>();
    public Object ExtraData = null;
    public ArrayList<String> CrewSets = new ArrayList<>();
    //public Color defalthighlihgt = Color.RED;


    public void applyExtraDataToCrew(){
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"applyExtraDataToCrew",0),this);
        CrewReplacer_Log.push();
        applyExtraDataToCrew(ExtraData);
        CrewReplacer_Log.pop();
    }
    public void applyExtraDataToCrew(Object newData){
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"applyExtraDataToCrew",1),this);
        CrewReplacer_Log.push();
        for(crewReplacer_Crew a: Crews){
            a.setExtraData(newData);
        }
        CrewReplacer_Log.pop();
    }
    public void applyExtraDataToCrewAndJob(Object newData){
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"applyExtraDataToCrewAndJob",0),this);
        CrewReplacer_Log.push();
        ExtraData = newData;
        applyExtraDataToCrew(newData);
        CrewReplacer_Log.pop();
    }
    public void resetExtraDataToCrewsAndJob(){
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"resetExtraDataToCrewsAndJob",0),this);
        CrewReplacer_Log.push();
        ExtraData = null;
        resetExtraDataToCrews();
        CrewReplacer_Log.pop();
    }
    public void resetExtraDataToCrews(){
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"resetExtraDataToCrews",0),this);
        CrewReplacer_Log.push();
        for(crewReplacer_Crew a: Crews){
            a.resetExtraData();
        }
        CrewReplacer_Log.pop();
    }

    public boolean addBlackListCrew(String crew, float loadPriority) {
        CrewReplacer_Log.loging(getIntoJobLog()+CrewReplacer_StringHelper.getLogString(className,"addBlackListCrew",0,crew,""+loadPriority),this);
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
    private boolean addBlackListCrew(CrewReplacer_BlackListCrew EC){
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"addBlackListCrew",4,EC.crew,""+EC.loadPriority),this);
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
        CrewReplacer_Log.loging(getIntoJobLog()+CrewReplacer_StringHelper.getLogString(className,"getBlackListCrewIfExists",0,crew),this);
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
        CrewReplacer_Log.loging(getIntoJobLog()+CrewReplacer_StringHelper.getLogString(className,"removeBlackListCrew",0,crew),this);
        CrewReplacer_Log.push();
        for(int a = 0; a < BlackListCrews.size(); a++){
            if (BlackListCrews.get(a).crew.equals(crew)) {
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"removeBlackListCrew",1),this);
                CrewReplacer_Log.pop();
                BlackListCrews.remove(a);
                return true;
            }
        }
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"removeBlackListCrew",2),this);
        CrewReplacer_Log.pop();
        return false;
    }

    public boolean hasCrew(String crew){
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"hasCrew",0),this);
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
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"getCrew",0),this);
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
            output = this.createDefaultCrew();
            output.name = crew;
            Crews.add(output);
        }
        CrewReplacer_Log.pop();
        return output;
    }
    public boolean removeCrew(String crew){
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"removeCrew",0),this);
        CrewReplacer_Log.push();
        //boolean output = false;
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew)){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"removeCrew",1,Crews.get(a).name),this);
                Crews.remove(a);
                organizePriority();//for now. will have to change later?
                CrewReplacer_Log.pop();
                return true;
            }
        }
        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"removeCrew",2,crew),this);
        CrewReplacer_Log.pop();
        return false;
    }
    public boolean addCrew(crewReplacer_Crew crew){
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"addCrew",0),this);
        CrewReplacer_Log.push();
        for(CrewReplacer_BlackListCrew a : BlackListCrews){
            if (a.crew.equals(crew.name)){
                if (a.loadPriority >= crew.crewLoadPriority){
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addCrew",4,""+a.loadPriority,""+crew.crewLoadPriority),this);
                    CrewReplacer_Log.pop();
                    return false;//?
                }else{
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"addCrew",5,""+a.loadPriority,""+crew.crewLoadPriority),this);
                    break;
                    //continue
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
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"addNewCrew",0,crew,""+crewPower,""+crewPriority,""+loadPriority),this);
        CrewReplacer_Log.push();
        crewReplacer_Crew a = this.createDefaultCrew();//new crewReplacer_Crew();
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

    public crewReplacer_Crew createDefaultCrew(){
        return new crewReplacer_Crew();
    }

    public boolean addCrewSet(String CrewSet){
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"addCrewSet",0),this);
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
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"removeCrewSet",0),this);
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
    public void applyCrewSets(){
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"applyCrewSets",0),this,true);
        CrewReplacer_Log.push();
        for (String a : CrewSets){
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"applyCrewSets",1,""+a),this);
            CrewReplacer_Log.push();
            crewReplacer_CrewSet jobSet = crewReplacer_Main.getCrewSet(a).getAllLinkedCrewsSets();
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"applyCrewSets",2),this);
            CrewReplacer_Log.push();
            for(CrewReplacer_BlackListCrew crew : jobSet.BlackListCrews){
                this.addBlackListCrew(crew.crew,crew.loadPriority);
            }
            CrewReplacer_Log.pop();
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"applyCrewSets",3),this);
            CrewReplacer_Log.push();
            for(crewReplacer_Crew crew : jobSet.Crews){
                try {
                    if (crew instanceof CrewReplacer_BlankCrew) {
                        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className, "applyCrewSets", 6, crew.name), this);
                        this.addNewCrew(crew.name, crew.crewPower, crew.crewDefence, crew.crewPriority, crew.crewLoadPriority);
                    } else {
                        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className, "applyCrewSets", 5, crew.name), this);
                        this.addCrew(crew);
                    }
                }catch (Exception e){
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className, "applyCrewSets", 7,true, crew.name,this.name,e.toString()), this,true);
                }
            }
            CrewReplacer_Log.pop();

            /*
            for(CrewReplacer_BlackListCrew crew : jobSet.BlackListCrews){
                this.addBlackListCrew(crew.crew,crew.loadPriority);
            }
            for(crewReplacer_Crew crew : jobSet.Crews){
                if(!hasCrew(crew.name)){
                    addCrew(crew);
                }else{
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"applyCrewSets",4),this);
                }
            }*/
            CrewReplacer_Log.pop();
        }
        CrewReplacer_Log.pop();
    }

    public void organizePriority(){
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"organizePriority",0),this);
        CrewReplacer_Log.push();
        crewPriority = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> temp = new ArrayList<Integer>();
        temp.add(0);
        crewPriority.add(temp);
        int min;
        int max;
        float look;
        Boolean done;
        for(int a = 1; a < Crews.size(); a++){
            //System.out.println("processing crew: " + a + "with a priority of: " + Crews.get(a).crewPriority);
            float priority = Crews.get(a).crewPriority;
            min = 0;
            max = crewPriority.size();
            done = false;
            while(max != min){
                look = (min + max) / 2;
                temp = crewPriority.get((int)look);
                float priority1 = Crews.get(temp.get(0)).crewPriority;
                if(priority < priority1){
                    int last = min;
                    min = (int)look;
                    if(last == min){
                        min++;
                    }
                }else if(priority > priority1){
                    max = (int)look;
                }else{
                    //System.out.println(a + " added to " + (int) look + " by 5");
                    temp = crewPriority.get((int)look);
                    temp.add(a);
                    crewPriority.set((int)look,temp);
                    done = true;
                    break;
                }
            }
            if(!done){
                temp = new ArrayList<Integer>();
                temp.add(a);
                if(max == crewPriority.size()){
                    float priority1 = Crews.get(crewPriority.get(max - 1).get(0)).crewPriority;
                    if(priority < priority1){
                        //System.out.println(a + " added at " + (max) + " by 6");
                        crewPriority.add(temp);
                    }else{
                        //System.out.println(a + " added at " + (max - 1) + " by 7");
                        crewPriority.add(max - 1,temp);
                    }
                }else{
                    float priority1 = Crews.get(crewPriority.get(max).get(0)).crewPriority;
                    if(priority < priority1){
                        //System.out.println(a + " added at " + (max + 1) + " by 8");
                        crewPriority.add(max + 1,temp);
                    }else{
                        //System.out.println(a + " added at " + (max) + " by 9");
                        crewPriority.add(max,temp);
                    }
                }
            }
        }
        CrewReplacer_Log.pop();
    }
    public float getAvailableCrewPower(CargoAPI cargo){
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + CrewReplacer_StringHelper.getLogString(className,"getAvailableCrewPower",0),this);
        CrewReplacer_Log.push();
        float output = 0;
        float temp;
        for(int a = 0; a < Crews.size(); a++){
            try {
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getAvailableCrewPower",1,Crews.get(a).name), this);
                temp = Crews.get(a).getCrewPowerInCargo(cargo);
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getAvailableCrewPower",2, ""+temp), this);
                output += temp;
            }catch (Exception e){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getAvailableCrewPower",3,true,""+e),this,true);
            }
            //output += (50);//462(50)//512(0)
        }
        CrewReplacer_Log.pop();
        return output;
    }
    public float[] getAvailableCrew(CargoAPI cargo){
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + CrewReplacer_StringHelper.getLogString(className,"getAvailableCrew",0),this);
        CrewReplacer_Log.push();
        float[] output = new float[Crews.size()];
        float temp = 0;
        for(int a = 0; a < Crews.size(); a++){
            try {
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getAvailableCrew",1,Crews.get(a).name),this);
                temp = Crews.get(a).getCrewInCargo(cargo);
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getAvailableCrew",2,""+temp),this);
                output[a] = temp;
            }catch (Exception e){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getAvailableCrew",3,true,""+e),this,true);
            }
        }
        CrewReplacer_Log.pop();
        return output;
    }


    public void automaticlyGetDisplayAndApplyCrewLost(CargoAPI cargo,int crewPowerRequired, float crew_power_to_lose,TextPanelAPI text){
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + CrewReplacer_StringHelper.getLogString(className,"automaticlyGetDisplayAndApplyCrewLost",0),this);
        CrewReplacer_Log.push();
        ArrayList<Float> crewUsed = getCrewForJob(cargo,crewPowerRequired);
        ArrayList<Float> crewLost = getCrewLost(cargo,crewUsed,crew_power_to_lose);
        applyCrewLost(crewLost,cargo);
        displayCrewLost(cargo,crewLost,text);
        CrewReplacer_Log.pop();
    }
    public ArrayList<Float> automaticlyGetAndApplyCrewLost(CargoAPI cargo, int crewPowerRequired, float crew_power_to_lose){
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + CrewReplacer_StringHelper.getLogString(className,"automaticlyGetAndApplyCrewLost",0),this);
        CrewReplacer_Log.push();
        ArrayList<Float> crewUsed = getCrewForJob(cargo,crewPowerRequired);
        ArrayList<Float> crewLost = getCrewLost(cargo,crewUsed,crew_power_to_lose);
        //displayCrewLost(crewLost,text,highlight);
        applyCrewLost(crewLost,cargo);
        CrewReplacer_Log.pop();
        return crewLost;
    }
    public ArrayList<Float> getCrewLost(CargoAPI cargo,ArrayList<Float> crewUsed, float crew_power_to_lose){//,CargoAPI cargo){
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"getCrewLost",0),this);
        CrewReplacer_Log.push();
        //crewUsed is an array of floats, each one equal to the amound of crew used on a given ID.
        //gets the number of crew lost, based on crew power. chocen randomly from an array of crew.
        //priority of crew dose not matter here. this is the crew lost in this task, form the crew that worked it.
        //outputs [crewid0 lost crew,crewid1 lost crew,crewid2 lost crew,extra]
        ArrayList<Float> output = new ArrayList<Float>();
       /* int JID = getID(jobs,job);
        if(JID == -1){
            return output;
        }*/
        ArrayList<Float> temp2 = new ArrayList<Float>();
        for(int a = 0; a < Crews.size(); a++){
            try {
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewLost",1,Crews.get(a).name),this);
                float temp = Crews.get(a).getCrewDefence(cargo);//cargo);
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewLost",2,""+temp),this);
                temp2.add(temp);
            }catch (Exception e){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewLost",3,true,""+e),this,true);
                temp2.add(Crews.get(a).crewPower);
            }
        }
        output = getRandomNumberList(crewUsed,temp2,crew_power_to_lose);
        float temp;
        for(int a = 0; a < output.size(); a++){
            try {
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewLost",4,Crews.get(a).name),this);
                float temp1 = Crews.get(a).getCrewToLose(cargo,crewUsed.get(a), output.get(a));
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewLost",5,""+temp1),this);
                output.set(a, temp1);//new system, to allow more contol.
            }catch (Exception e){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewLost",6,true,""+e),this,true);
                output.set(a,0f);
            }
            //temp = (float) Crews.get(a).getCrewLostPercent();
            //1,15,0 == 15//1,15,30 == 0
            //output.set(a,output.get(a) + (temp * Math.max(crewUsed.get(a) - output.get(a),0)));
        }
        CrewReplacer_Log.pop();
        return output;
    }
    public void displayCrewLost(CargoAPI cargo,ArrayList<Float> crewLost, TextPanelAPI text){
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"displayCrewLost",0),this);
        CrewReplacer_Log.push();
        //text.appendToLastParagraph(" runing display for:  " + name);//for testing.
        //text.appendToLastParagraph(" sizes: " + crewLost.size() + ", " + Crews.size());
        boolean last = false;
        for(int a = 0; a < crewLost.size(); a++){
            //String displayName = Crews.get(a).name;
            if(last && crewLost.get(a) >= 1){
                text.appendToLastParagraph(CrewReplacer_StringHelper.getString(className,"displayCrewLost",3));
            }
            if(crewLost.get(a) != 0){
                last = true;
                try {
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"displayCrewLost",1,Crews.get(a).name),this);
                    Crews.get(a).displayCrewLost(cargo,crewLost.get(a), text);
                }catch (Exception e){
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"displayCrewLost",2,true,""+e),this,true);
                }
            }
            /*if(crewLost.get(a) > 1){
                last = true;
                text.appendToLastParagraph(message[0] + "" + crewLost.get(a) + " " + displayName + "s");
                text.highlightInLastPara(highlight, displayName + "s");
            }else if(crewLost.get(a) == 1){
                last = true;
                text.appendToLastParagraph(message[1] + " " + displayName);
                text.highlightInLastPara(highlight, displayName);
            }*/
            CrewReplacer_Log.pop();
        }
    }
    public void applyCrewLost(ArrayList<Float> crewLost,CargoAPI cargo){
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + CrewReplacer_StringHelper.getLogString(className,"applyCrewLost",0),this);
        CrewReplacer_Log.push();
        for(int a = 0; a < Crews.size(); a++) {
            try {
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"applyCrewLost",1,Crews.get(a).name),this);
                Crews.get(a).removeCrew(cargo, crewLost.get(a));
            }catch (Exception e){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"applyCrewLost",2,""+e),this,true);
            }
            //String ComonadyName = Crews.get(a).name;
            //fleet.getCargo().removeCommodity(ComonadyName, crewLost.get(a));
        }
        CrewReplacer_Log.pop();
    }
    public ArrayList<Float> getCrewForJob(CargoAPI cargo, float crewPowerRequired){
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + CrewReplacer_StringHelper.getLogString(className,"getCrewForJob",0),this);
        CrewReplacer_Log.push();
        /*
            this is my changed getCrewForJob. it is compleat and somewhat tested..
            it worked in 3 repeating stages (starting from the second for loop):
            1: it gets the available crew in this priority.
            2: it sees if the crew power in this prioirity is more then what is required
            3a:it is more then required, in this case it randomly choses the required crew
            3b:it is less or equal to what is required. in this case, it adds available crew on this priority to the output.
         */
        ArrayList<Float> output = new ArrayList<Float>();
        for(int a = 0; a < Crews.size(); a++){
            output.add((float)0);//set output.size to the same length of crews, and make all values zero.
        }
        ArrayList<Integer> tempArray = new ArrayList<Integer>();
        for(int a = 0; a < crewPriority.size() && crewPowerRequired > 0; a++){
            //sets availbe crew power of this priority to output
            tempArray = crewPriority.get(a);
            ArrayList<Float> crewTemp = new ArrayList<Float>();
            float power = 0;
            //1
            for(int b = 0; b < tempArray.size(); b++) {
                try {
                    int index = tempArray.get(b);
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewForJob",1,Crews.get(index).name),this);
                    CrewReplacer_Log.push();
                    float temp = Crews.get(index).getCrewInCargo(cargo);
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewForJob",2,""+temp),this);
                    crewTemp.add(temp);
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewForJob",3,""+temp),this);
                    temp = Crews.get(index).getCrewPowerInCargo(cargo);
                    power += temp;
                    CrewReplacer_Log.pop();
                }catch (Exception e){
                    CrewReplacer_Log.pop();
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewForJob",4,true,""+e),this,true);
                    crewTemp.add(0f);
                }
            }
            //2
            if (power > crewPowerRequired){
                //3a
                ArrayList<Float> powerTemp = new ArrayList<Float>();
                for(int b = 0; b < tempArray.size(); b++) {
                    try {
                        int index = tempArray.get(b);
                        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewForJob",5,Crews.get(index).name),this);
                        float temp = Crews.get(index).getCrewPower(cargo);//cargo);
                        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewForJob",6,""+temp),this);
                        powerTemp.add(temp);
                    }catch (Exception e){
                        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewForJob",7,true,""+e),this,true);
                        powerTemp.add(0f);
                    }
                }
                crewTemp = getRandomNumberList(crewTemp,powerTemp,crewPowerRequired);
                crewPowerRequired = 0;
            }
            //3b
            crewPowerRequired -= power;
            for(int b = 0; b < tempArray.size(); b++){
                int index = tempArray.get(b);
                output.set(index,crewTemp.get(b));
            }
        }
        //already have all other items in array set to zero, so this ends here.
        CrewReplacer_Log.pop();
        return output;
    }

    public void displayCrewAvailable(CargoAPI cargo,TextPanelAPI text){
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + CrewReplacer_StringHelper.getLogString(className,"displayCrewAvailable",0),this);
        CrewReplacer_Log.push();
        for(int a = 0; a < Crews.size(); a++){
            try {
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"displayCrewAvailable",1,Crews.get(a).name),this);
                float crewInCargo = Crews.get(a).getCrewInCargo(cargo);
                if(crewInCargo != 0) {
                    Crews.get(a).displayCrewAvailable(cargo, crewInCargo, text);
                }
            }catch (Exception e){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"displayCrewAvailable",2,true,""+e),this,true);
            }
            /*float crew = Crews.get(a).getCrewInFleet(fleet);
            if(crew == 1){
                text.appendToLastParagraph(message[0] + "" + crew + " " + Crews.get(a).name + "s");
            }else if(crew != 0){
                text.appendToLastParagraph(message[1] + "" + crew + " " + Crews.get(a).name);
            }*/
        }
        CrewReplacer_Log.pop();
    }

    public String[] GetCrewNames(){
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"GetCrewNames",0),this);
        CrewReplacer_Log.push();
        String[] output = new String[Crews.size()];
        for(int a = 0; a < Crews.size(); a++){
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"GetCrewNames",1,name),this);
            output[a] = Crews.get(a).name;
        }
        CrewReplacer_Log.pop();
        return output;
    }
    public String[] getCrewDisplayNames(CargoAPI cargo){
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + CrewReplacer_StringHelper.getLogString(className,"getCrewDisplayNames",0),this);
        CrewReplacer_Log.push();
        String[] output = new String[Crews.size()];
        for(int a = 0; a < Crews.size(); a++){
            try {
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewDisplayNames",1,Crews.get(a).name),this);
                String temp = Crews.get(a).getDisplayName(cargo);
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewDisplayNames",2,temp),this);
                output[a] = temp;
            }catch (Exception e){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCrewDisplayNames",3,true,""+e),this,true);
            }
        }
        CrewReplacer_Log.pop();
        return output;
    }

    public float getCargoSpaceUsed(CargoAPI cargo,String cargoType){
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceUsed",0,cargoType),this);
        CrewReplacer_Log.push();
        float output = 0;
        for(crewReplacer_Crew Crew : this.Crews) {
            try {
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceUsed",1,Crew.name), this);
                float temp = 0;
                switch (cargoType) {
                    case CARGO_CARGO:
                        temp = Crew.getCargoSpaceUse(cargo);
                        break;
                    case CARGO_FUEL:
                        temp = Crew.getFuelSpaceUse(cargo);
                        break;
                    case CARGO_CREW:
                        temp = Crew.getCrewSpaceUse(cargo);
                        break;
                }
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceUsed",2,""+temp), this);
                output += temp;
            }catch (Exception e){
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceUsed",3,true,""+e),this,true);
            }
        }
        CrewReplacer_Log.pop();
        return output;
    }
    public float[] getCargoSpaceRange(CargoAPI cargo,float power,boolean includeDefence,String cargoType){
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",0,includeDefence),this);
        CrewReplacer_Log.push();
        float[] output = new float[2];
        //ArrayList<Float> CrewUsed = this.getAvailableCrew(CargoAPI cargo);
        //this.crewPriority;//NOTE: first crew i use is prioity 0. then it goes up from there.
        for(ArrayList<Integer> priority : this.crewPriority){
            CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",1),this);
            CrewReplacer_Log.push();
            ArrayList<Float> cargoSpacesPerPower = new ArrayList<Float>();//cargo space per power.
            //ArrayList<Float> powers = new ArrayList<Float>();
            /*so here what i need to do for each cargo set:
            * 1) determin the max numger of items i can get. max this number at power / Crew.getPower()
            * 2) determin the cargo of off the max number of items i can get (for each item)
            * 3) for each item, cargoSpace / power is the amount of cargo per power.
            * 4.a) run though if power of this set >= power, add all cargo spaces to largest and smallest output and power -= power of this set.
            * 4.b) if power of this set < power, get the total number of power required, and find the largest and smallest possable combonations of cargo that use all the power.
            *   -how though?
            *   1) organize items in cargo pew power. from least to greatest.
            *   2) move first up, then down the array, getting the amount of power required until >= power, then lower the amount of that crew until its <= that power*/
            float powerTemp = 0;
            float cargoTemp = 0;
            for(int ID : priority){
                crewReplacer_Crew Crew = this.Crews.get(ID);
                try {
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",2,Crew.name),this);
                    float tempa = 0;
                    float tempc = 0;
                    float tempd;
                    switch (cargoType) {
                        case CARGO_CARGO:
                            tempa = Crew.getCargoSpaceUse(cargo);
                            tempc = Crew.getCrewPower(cargo);
                            tempd = Crew.getCargoSpacePerItem(cargo);
                            if(tempd != 0) {
                                tempc /= tempd;
                            }
                            break;
                        case CARGO_FUEL:
                            tempa = Crew.getFuelSpaceUse(cargo);
                            tempc = Crew.getCrewPower(cargo);
                            tempd = Crew.getFuelSpacePerItem(cargo);
                            if(tempd != 0) {
                                tempc /= tempd;
                            }
                            break;
                        case CARGO_CREW:
                            tempa = Crew.getCrewSpaceUse(cargo);
                            tempc = Crew.getCrewPower(cargo);
                            tempd = Crew.getFuelSpacePerItem(cargo);
                            if(tempd != 0) {
                                tempc /= tempd;
                            }
                            break;
                    }//power / cargo
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",3,""+tempa),this);
                    if(includeDefence){
                        float tempDefence = Crew.getCrewDefence(cargo) / Crew.getCrewPower(cargo);
                        float tempbT =  tempa / tempDefence;
                        float tempcT = tempc / tempDefence;
                        tempa = Math.min(tempa,tempbT);//this exsists to prevent crew with a defence less then 1, from increaseing the apparent amount of cargo that the crew is useing past how mush is in the cargo at all.
                        tempc = tempcT;//Math.min(tempc,tempcT);
                        tempc /= Crew.getCrewPower(cargo);//10p,5d,2c. 5 / 10 = 0.5.   tempc = 2 / 0.5 = 4. 4 / 10 = 0.4
                    }
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",4,""+tempa),this);
                    float tempb = Crew.getCrewPowerInCargo(cargo);
                    powerTemp+= tempb;
                    cargoTemp+= tempa;
                    cargoSpacesPerPower.add(tempc);
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",5,""+tempb,""+tempa),this);
                }catch (Exception e){
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",6,this.Crews.get(ID).name),this);
                    //powers.add(0f);
                    cargoSpacesPerPower.add(0f);
                }

            }
            //CrewReplacer_Log.pop();
            /* current data status:
            * powers = the power per item of a given crew.
            * cargoSpaces = the cargospace per item of a given crew (possibly including defence of said crew.)*/

            if(power < powerTemp){
                ArrayList<Float> IDTemp = new ArrayList<>();
                for(int a = 0; a < priority.size(); a++) {
                    IDTemp.add((float)priority.get(a));
                }
                ArrayList<Float> sortedID = CrewReplacer_organizeArrayCode.sortArray(IDTemp,cargoSpacesPerPower).get(0);
                powerTemp = power;
                cargoTemp = 0;
                for(int a = 0; a < sortedID.size(); a++){
                    int id = (int)(float)sortedID.get(a);
                    float crewPowerTemp = Crews.get(id).getCrewPowerInCargo(cargo);
                    if(crewPowerTemp > powerTemp){
                        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",7,""+crewPowerTemp,""+powerTemp),this);
                        float tempa=0;
                        float tempb = powerTemp / crewPowerTemp;//500 req pow / 750 have power = 0.75
                        float tempc = Math.round(0.499+(Crews.get(id).getCrewInCargo(cargo) * tempb));
                        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",8,""+tempa,""+tempb,""+tempc),this);
                        switch (cargoType) {
                            case CARGO_CARGO:
                                tempa = Crews.get(id).getCargoSpaceUse(cargo,tempc);
                                break;
                            case CARGO_FUEL:
                                tempa = Crews.get(id).getFuelSpaceUse(cargo,tempc);
                                break;
                            case CARGO_CREW:
                                tempa = Crews.get(id).getCrewSpaceUse(cargo,tempc);
                                break;
                        }
                        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",9,""+tempa,""+tempb,""+tempc),this);
                        if(includeDefence){
                            float tempbT =  tempa / (Crews.get(id).getCrewDefence(cargo) / Crews.get(id).getCrewPower(cargo));
                            tempa = Math.min(tempa,tempbT);//this exsists to prevent crew with a defence less then 1, from increaseing the apparent amount of cargo that the crew is useing past how mush is in the cargo at all.
                        }
                        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",10,""+tempa,""+tempb,""+tempc),this);
                        cargoTemp += tempa;
                        break;
                    }
                    float[] temps = {Crews.get(id).getCrewPowerInCargo(cargo),Crews.get(id).getCargoSpaceUse(cargo)};
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",11,""+temps[0],""+temps[1]),this);
                    powerTemp -= temps[0];
                    cargoTemp += temps[1];
                }
                output[0] += cargoTemp;
                //CrewReplacer_Log.loging("CARGO TEMP: "+cargoTemp,this,true);
                powerTemp = power;
                cargoTemp = 0;
                for(int a = sortedID.size() - 1; a >= 0; a--){
                    int id = (int)(float)sortedID.get(a);
                    float crewPowerTemp = Crews.get(id).getCrewPowerInCargo(cargo);
                    if(crewPowerTemp > powerTemp){
                        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",12,""+crewPowerTemp,""+powerTemp),this);
                        float tempa=0;
                        float tempb = powerTemp / crewPowerTemp;//500 req pow / 750 have power = 0.75
                        float tempc = Math.round(0.499+(Crews.get(id).getCrewInCargo(cargo) * tempb));
                        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",13,""+tempa,""+tempb,""+tempc),this);
                        switch (cargoType) {
                            case CARGO_CARGO:
                                tempa = Crews.get(id).getCargoSpaceUse(cargo,tempc);
                                break;
                            case CARGO_FUEL:
                                tempa = Crews.get(id).getFuelSpaceUse(cargo,tempc);
                                break;
                            case CARGO_CREW:
                                tempa = Crews.get(id).getCrewSpaceUse(cargo,tempc);
                                break;
                        }
                        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",14,""+tempa,""+tempb,""+tempc),this);
                        if(includeDefence){
                            float tempbT =  tempa / (Crews.get(id).getCrewDefence(cargo) / Crews.get(id).getCrewPower(cargo));
                            tempa = Math.min(tempa,tempbT);//this exsists to prevent crew with a defence less then 1, from increaseing the apparent amount of cargo that the crew is useing past how mush is in the cargo at all.
                        }
                        CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",15,""+tempa,""+tempb,""+tempc),this);
                        cargoTemp += tempa;
                        break;
                    }
                    float[] temps = {Crews.get(id).getCrewPowerInCargo(cargo),Crews.get(id).getCargoSpaceUse(cargo)};
                    CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",16,""+temps[0],""+temps[1]),this);
                    powerTemp -= temps[0];
                    cargoTemp += temps[1];
                }
                output[1] += cargoTemp;
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",17,true,""+cargoTemp),this,true);
                CrewReplacer_Log.pop();
                CrewReplacer_Log.pop();
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",18,""+output[0],""+output[1]),this);
                return output;
            }
            output[0] += cargoTemp;
            output[1] += cargoTemp;
            power -= powerTemp;
            CrewReplacer_Log.pop();
            if(power <= 0){
                CrewReplacer_Log.pop();
                CrewReplacer_Log.loging(CrewReplacer_StringHelper.getLogString(className,"getCargoSpaceRange",19,""+output[0],""+output[1]),this);
                return output;
            }
        }
        CrewReplacer_Log.pop();
        return output;
    }

    private ArrayList<Float> getRandomNumberList(ArrayList<Float> maxCrew,ArrayList<Float> power,float input){
        CrewReplacer_Log.loging(getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"getRandomNumberList",0),this);
        CrewReplacer_Log.push();
        //edited, inproved, and tested. only returns hole numbers now. still in flpat formthough cause im lazzy.

        //can missround numbers sometimes, so it ca be not 100% random, but close enuth
        //the recursive loop might be ponitless. i have never seen it do anything since i changes this loop to take hole numbers.
        //input: an array of numbers (crew),an array of more numbers(crew power) and an float(required power).
        //looks at the array of numbers, and randomly removes numbers untill input numbers have ben removed.
        //the randomness is basses on number of items in loop, hence the complecity.

        //proldom: the end while loop (to add numbers to the output) can fire a lot, if i only have two maxcrew types and get unlucky.
        //proldom: at high numbers of maxCrew types, i can overrull by one or two numbers of crew, dew to rounding. dont know how to fix that safely
        //never saw more then 2
        //init data
        float total = 0;
        float remainder = 0;
        float totalrandom = 0;
        float randomness = 0;
        ArrayList<Float> output = new ArrayList<Float>();
        ArrayList<Float> temp = new ArrayList<Float>();
        //get combined total of all numbers in maxCrew
        //get random numbers for every max number. set to zero if there is nothing for said max number (also remember total of all the random numbers)
        for(int a = 0; a < maxCrew.size(); a++){
            total += maxCrew.get(a) * power.get(a);

            if(maxCrew.get(a) == 0){
                temp.add((float)0);
            }else{
                temp.add((float)Math.random());
            }
            totalrandom += temp.get(a);
        }
        //min = power.get((int)min);
        //if maxCrew is grater then input, output maxCrew.
        if(total <= input){
            for(int a = 0; a < maxCrew.size(); a++){
                output.add(maxCrew.get(a));
            }
            CrewReplacer_Log.pop();
            return output;
        }
        //get random numbers for every max number. set to zero if there is nothing for said max number (also remember total of all the random numbers)
        //get the prodect of random numbers and input numbers, then devide by there total (get the prodect of a given slots % of there given values)
        for(int a = 0; a < temp.size(); a++){
            temp.set(a,((temp.get(a)) / totalrandom) * (((maxCrew.get(a) * power.get(a))) / total));
            randomness += temp.get(a);
        }
        //set temp to a % value of its self, so the sum is equal to 1. (or 100%), and multaply by input, to get required power from eatch crew type
        for(int a = 0; a < temp.size(); a++){
            temp.set(a,(temp.get(a) / randomness) * input);
        }
        //sets the output. if any output is grater then its input, change the output to be == to its input. remeber all outputs
        remainder = input;
        //total = 0;
        //int itemsCheack = 0;
        for(int a = 0; a < temp.size(); a++){
            float temp1 = temp.get(a) / power.get(a);//crew i want
            float temp2 = (float)Math.floor(temp1);
            temp1 = temp1 - temp2;//temp2 == crew here, temp1 == extra crew here to spend
            float temp3 = maxCrew.get(a);
            //if crew i want is graer then crew i have
            if((int)temp3 < temp1 + temp2){
                //System.out.println("path a");
                output.add((float)((int)temp3));
            }else{
                output.add(temp2);
                //System.out.println("reducing cost by: " + (temp2));
            }
            remainder -= (output.get(a) * power.get(a));
        }
        //if my code failed to do ANYTHING AT ALL, it gets one crew from each category that has crew remaining.
        while (remainder > 0) {//added as a test to try and remove the recursive loop
            //System.out.println("runing that loop again hahah");
            //total = 0;
            for (int a = 0; a < output.size() && remainder > 0; a++) {
                if (maxCrew.get(a) - output.get(a) != 0) {
                    output.set(a, output.get(a) + 1);
                    remainder -= (power.get(a));
                    //System.out.println("    " + a);

                    //System.out.println("remainder:" + remainder);
                    //temp.set(a,(maxCrew.get(a) - output.get(a)));
                    /*total += (maxCrew.get(a) - output.get(a)) * power.get(a);*/
                }
            }
            /*for(int a = 0;a < output.size(); a++){
                // crewpower / total    my_computation/remainder
                float temp1 = (float)(((remainder * ((maxCrew.get(a) - output.get(a)) * power.get(a))) / total) / power.get(a));
                output.set(a,output.get(a) + temp1);
                remainder -= temp1 * (temp1 * power.get(a));
            }*/
        }
        //}
        //if i didnt get enuth crew, run this code again, with the leftover crew, and leftover required input
        /*if(remainder > 0){
            temp = getRandomNumberList(temp,power,remainder);
            for(int a = 0; a < output.size(); a++){
                output.set(a,output.get(a) + temp.get(a));
            }
        }*/
        CrewReplacer_Log.pop();
        return output;
    }

    private String getIntoJobLog(){
        return CrewReplacer_StringHelper.getLogString(className,"getIntoJobLog",0,name);
    }
    private String getIntoJobLog(CargoAPI cargo){
        try {
            return getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"getIntoJobLog",1,cargo.getFleetData().getFleet().getName());
        }catch (Exception e){
            return getIntoJobLog() + CrewReplacer_StringHelper.getLogString(className,"getIntoJobLog",2);
        }
    }

    /*here is the older functions*/

    public float getAvailableCrewPower(CampaignFleetAPI fleet){
        return getAvailableCrewPower(fleet.getCargo());
    }
    public float[] getAvailableCrew(CampaignFleetAPI fleet){
        return getAvailableCrew(fleet.getCargo());
    }
    public void automaticlyGetDisplayAndApplyCrewLost(CampaignFleetAPI fleet,int crewPowerRequired, float crew_power_to_lose,TextPanelAPI text){
        automaticlyGetDisplayAndApplyCrewLost(fleet.getCargo(),crewPowerRequired,crew_power_to_lose,text);
    }
    public ArrayList<Float> automaticlyGetAndApplyCrewLost(CampaignFleetAPI fleet, int crewPowerRequired, float crew_power_to_lose){
        return automaticlyGetAndApplyCrewLost(fleet.getCargo(),crewPowerRequired,crew_power_to_lose);
    }
    public void applyCrewLost(ArrayList<Float> crewLost,CampaignFleetAPI fleet){
        applyCrewLost(crewLost,fleet.getCargo());
    }
    public ArrayList<Float> getCrewForJob(CampaignFleetAPI fleet, float crewPowerRequired){
        return getCrewForJob(fleet.getCargo(),crewPowerRequired);
    }
    public void displayCrewAvailable(CampaignFleetAPI fleet,TextPanelAPI text){
        displayCrewAvailable(fleet.getCargo(),text);
    }
}
