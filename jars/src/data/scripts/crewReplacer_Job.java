package data.scripts;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;

import java.util.ArrayList;

public class crewReplacer_Job {
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
    public String name;
    public ArrayList<ArrayList<Integer>> crewPriority;//organized greatest to lowest.
    public ArrayList<crewReplacer_Crew> Crews = new ArrayList<crewReplacer_Crew>();
    public Object ExtraData = null;
    //public Color defalthighlihgt = Color.RED;
    public void applyExtraDataToCrew(){
        CrewReplacer_Log.loging(getIntoJobLog() + "running applyExtraDataToCrew",this);
        CrewReplacer_Log.push();
        applyExtraDataToCrew(ExtraData);
        CrewReplacer_Log.pop();
    }
    public void applyExtraDataToCrew(Object newData){
        CrewReplacer_Log.loging(getIntoJobLog() + "running applyExtraDataToCrew",this);
        CrewReplacer_Log.push();
        for(crewReplacer_Crew a: Crews){
            a.setExtraData(newData);
        }
        CrewReplacer_Log.pop();
    }
    public void applyExtraDataToCrewAndJob(Object newData){
        CrewReplacer_Log.loging(getIntoJobLog() + "running applyExtraDataToCrewAndJob",this);
        CrewReplacer_Log.push();
        ExtraData = newData;
        applyExtraDataToCrew(newData);
        CrewReplacer_Log.pop();
    }
    public void resetExtraDataToCrewsAndJob(){
        ExtraData = null;
        resetExtraDataToCrews();
    }
    public void resetExtraDataToCrews(){
        CrewReplacer_Log.loging(getIntoJobLog() + "running resetExtraDataToCrews",this);
        CrewReplacer_Log.push();
        for(crewReplacer_Crew a: Crews){
            a.resetExtraData();
        }
        CrewReplacer_Log.pop();
    }

    public crewReplacer_Crew getCrew(String crew){
        CrewReplacer_Log.loging(getIntoJobLog() + "running getCrew",this);
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
        CrewReplacer_Log.loging(getIntoJobLog() + "running Remove",this);
        CrewReplacer_Log.push();
        //boolean output = false;
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew)){
                CrewReplacer_Log.loging("crew found. removing crew named: " + Crews.get(a).name,this);
                Crews.remove(a);
                organizePriority();//for now. will have to change later?
                CrewReplacer_Log.pop();
                return true;
            }
        }
        CrewReplacer_Log.loging("no crew found. cannot remove crew named: " + crew,this);
        CrewReplacer_Log.pop();
        return false;
    }
    public boolean addCrew(crewReplacer_Crew crew){
        CrewReplacer_Log.loging(getIntoJobLog() + "running Add Crew",this);
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
        CrewReplacer_Log.loging(getIntoJobLog() + "trying to add new crew named: " + crew + " wtih a power & priority of: " + crewPower + " & " + crewPriority,this);
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
        CrewReplacer_Log.loging(getIntoJobLog() + "running Merge Crew... setting stats to name: " + crew.name + ", power: " + crewPower + ", defence: " + crewDefence + ", priority: " + crewPriority,this);
        crew.crewPower = crewPower;
        crew.crewDefence = crewDefence;
        crew.crewPriority = crewPriority;
        //crew.maxLosePercent = crewMaxLosePercent;
        //crew.maxLosePercent = crewMinLosePercent;
        //crew.NormalLossRules = crewNormalLoseRules;
    }


    public void organizePriority(){
        CrewReplacer_Log.loging(getIntoJobLog() + "running Organizing Priority",this);
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
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + "running Getting Available Crew Power",this);
        CrewReplacer_Log.push();
        float output = 0;
        float temp;
        for(int a = 0; a < Crews.size(); a++){
            try {
                CrewReplacer_Log.loging("getting crew named: " + Crews.get(a).name, this);
                temp = Crews.get(a).getCrewPowerInCargo(cargo);
                CrewReplacer_Log.loging("adding " + temp + " crew power", this);
                output += temp;
            }catch (Exception e){
                CrewReplacer_Log.loging("ERROR! failed to get crew power in fleet!!! exception type: " + e,this,true);
            }
            //output += (50);//462(50)//512(0)
        }
        CrewReplacer_Log.pop();
        return output;
    }
    public float[] getAvailableCrew(CargoAPI cargo){
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + "running Get Available Crew",this);
        CrewReplacer_Log.push();
        float[] output = new float[Crews.size()];
        float temp = 0;
        for(int a = 0; a < Crews.size(); a++){
            try {
                CrewReplacer_Log.loging("getting crew named: " + Crews.get(a).name,this);
                temp = Crews.get(a).getCrewInCargo(cargo);
                CrewReplacer_Log.loging("got " + temp + " crew",this);
                output[a] = temp;
            }catch (Exception e){
                CrewReplacer_Log.loging("ERROR! failed to get crew numbers in fleet!!! exception type: " + e,this,true);
            }
        }
        CrewReplacer_Log.pop();
        return output;
    }


    public void automaticlyGetDisplayAndApplyCrewLost(CargoAPI cargo,int crewPowerRequired, float crew_power_to_lose,TextPanelAPI text){
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + "running Automatically Get Display And Apply Crew Lost...",this);
        CrewReplacer_Log.push();
        ArrayList<Float> crewUsed = getCrewForJob(cargo,crewPowerRequired);
        ArrayList<Float> crewLost = getCrewLost(cargo,crewUsed,crew_power_to_lose);
        displayCrewLost(cargo,crewLost,text);
        applyCrewLost(crewLost,cargo);
        CrewReplacer_Log.pop();
    }
    public ArrayList<Float> automaticlyGetAndApplyCrewLost(CargoAPI cargo, int crewPowerRequired, float crew_power_to_lose){
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + "running Automaticly Get And Apply Crew Lost...",this);
        CrewReplacer_Log.push();
        ArrayList<Float> crewUsed = getCrewForJob(cargo,crewPowerRequired);
        ArrayList<Float> crewLost = getCrewLost(cargo,crewUsed,crew_power_to_lose);
        //displayCrewLost(crewLost,text,highlight);
        applyCrewLost(crewLost,cargo);
        CrewReplacer_Log.pop();
        return crewLost;
    }
    public ArrayList<Float> getCrewLost(CargoAPI cargo,ArrayList<Float> crewUsed, float crew_power_to_lose){//,CargoAPI cargo){
        CrewReplacer_Log.loging(getIntoJobLog() + "running getCrewLost...",this);
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
                CrewReplacer_Log.loging("getting crew power for crew named: " + Crews.get(a).name,this);
                float temp = Crews.get(a).getCrewDefence(cargo);//cargo);
                CrewReplacer_Log.loging("got " + temp + " defencive strength",this);
                temp2.add(temp);
            }catch (Exception e){
                CrewReplacer_Log.loging("ERROR!!! failed to get crew power for crew with function getCrewPower(). getting variable instead. Exception type: " + e,this,true);
                temp2.add(Crews.get(a).crewPower);
            }
        }
        output = getRandomNumberList(crewUsed,temp2,crew_power_to_lose);
        float temp;
        for(int a = 0; a < output.size(); a++){
            try {
                CrewReplacer_Log.loging("getting crew lost for new named: " + Crews.get(a).name,this);
                float temp1 = Crews.get(a).getCrewToLose(cargo,crewUsed.get(a), output.get(a));
                CrewReplacer_Log.loging("got " + temp1 + " crew lost",this);
                output.set(a, temp1);//new system, to allow more contol.
            }catch (Exception e){
                CrewReplacer_Log.loging("ERROR!!! failed to get crew lost. setting crew lost to zero for temp data... exception type: " + e,this,true);
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
        CrewReplacer_Log.loging(getIntoJobLog() + "running Display Crew Lost...",this);
        CrewReplacer_Log.push();
        //text.appendToLastParagraph(" runing display for:  " + name);//for testing.
        //text.appendToLastParagraph(" sizes: " + crewLost.size() + ", " + Crews.size());
        boolean last = false;
        for(int a = 0; a < crewLost.size(); a++){
            //String displayName = Crews.get(a).name;
            if(last && crewLost.get(a) >= 1){
                text.appendToLastParagraph(", ");
            }
            if(crewLost.get(a) != 0){
                last = true;
                try {
                    CrewReplacer_Log.loging("running DisplayCrewNumbers for crew named " + Crews.get(a).name,this);
                    Crews.get(a).displayCrewLost(cargo,crewLost.get(a), text);
                }catch (Exception e){
                    CrewReplacer_Log.loging("ERROR!!! failed to display crew numbers. Exception type: " + e,this,true);
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
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + "running Apply Crew Lost",this);
        CrewReplacer_Log.push();
        for(int a = 0; a < Crews.size(); a++) {
            try {
                CrewReplacer_Log.loging("removing crew for crew named: " + Crews.get(a).name,this);
                Crews.get(a).removeCrew(cargo, crewLost.get(a));
            }catch (Exception e){
                CrewReplacer_Log.loging("ERROR!!! failed to remove crew. Exception type: " + e,this,true);
            }
            //String ComonadyName = Crews.get(a).name;
            //fleet.getCargo().removeCommodity(ComonadyName, crewLost.get(a));
        }
        CrewReplacer_Log.pop();
    }
    public ArrayList<Float> getCrewForJob(CargoAPI cargo, float crewPowerRequired){
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + "running Get Crew For Job...",this);
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
                    CrewReplacer_Log.loging("fining avialbe crew and power for crew named " + Crews.get(index).name,this);
                    CrewReplacer_Log.push();
                    float temp = Crews.get(index).getCrewInCargo(cargo);
                    CrewReplacer_Log.loging("crew in fleet: " + temp,this);
                    crewTemp.add(temp);
                    CrewReplacer_Log.loging("crew power in fleet: " + temp,this);
                    temp = Crews.get(index).getCrewPowerInCargo(cargo);
                    power += temp;
                    CrewReplacer_Log.pop();
                }catch (Exception e){
                    CrewReplacer_Log.pop();
                    CrewReplacer_Log.loging("ERROR!!! could not get crewInFleet,orCrewPowerInFleet. setting available crew and power to zero for this function. Exception type: " + e,this,true);
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
                        CrewReplacer_Log.loging("getting crew power for crew named: " + Crews.get(index).name,this);
                        float temp = Crews.get(index).getCrewPower(cargo);//cargo);
                        CrewReplacer_Log.loging("crew power: " + temp,this);
                        powerTemp.add(temp);
                    }catch (Exception e){
                        CrewReplacer_Log.loging("ERROR!!! failed to get crew power. setting power to zero for this function Exception type " + e,this,true);
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
        CrewReplacer_Log.loging(getIntoJobLog(cargo) + "running Display Crew Available",this);
        CrewReplacer_Log.push();
        for(int a = 0; a < Crews.size(); a++){
            try {
                CrewReplacer_Log.loging("displaying crew numbers for crew named: " + Crews.get(a).name,this);
                float crewInCargo = Crews.get(a).getCrewInCargo(cargo);
                if(crewInCargo != 0) {
                    Crews.get(a).displayCrewAvailable(cargo, crewInCargo, text);
                }
            }catch (Exception e){
                CrewReplacer_Log.loging("ERROR!!! failed to display crew numbers. Exception type: " + e,this,true);
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
        CrewReplacer_Log.loging(getIntoJobLog() + "running Get Crew Names",this);
        CrewReplacer_Log.push();
        String[] output = new String[Crews.size()];
        for(int a = 0; a < Crews.size(); a++){
            CrewReplacer_Log.loging("getting crew named: " + name,this);
            output[a] = Crews.get(a).name;
        }
        CrewReplacer_Log.pop();
        return output;
    }
    public String[] getCrewDisplayNames(CargoAPI cargo){
        CrewReplacer_Log.loging("running Get Crew Display Names",this);
        CrewReplacer_Log.push();
        String[] output = new String[Crews.size()];
        for(int a = 0; a < Crews.size(); a++){
            try {
                CrewReplacer_Log.loging("getting display name for crew named: " + Crews.get(a).name,this);
                String temp = Crews.get(a).getDisplayName(cargo);
                CrewReplacer_Log.loging("got name " + temp,this);
                output[a] = temp;
            }catch (Exception e){
                CrewReplacer_Log.loging("ERROR!!! failed to get display name. Exception type "+ e,this,true);
            }
        }
        CrewReplacer_Log.pop();
        return output;
    }

    private ArrayList<Float> getRandomNumberList(ArrayList<Float> maxCrew,ArrayList<Float> power,float input){
        CrewReplacer_Log.loging(getIntoJobLog() + "getting random number list...",this);
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
        return "in job named '" + name + "' ";
    }
    private String getIntoJobLog(CampaignFleetAPI fleet){
        try {
            return getIntoJobLog() + "in fleet named '" + fleet.getName() + "' ";
        }catch (Exception e){
            return getIntoJobLog() + "in fleet named " + "'ERROR!!!'" + " ";
        }
    }
    private String getIntoJobLog(CargoAPI cargo){
        try {
            return getIntoJobLog() + "in fleet named '" + cargo.getFleetData().getFleet().getName() + "' ";
        }catch (Exception e){
            return getIntoJobLog() + "in fleet named " + "'ERROR!!!'" + " ";
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
