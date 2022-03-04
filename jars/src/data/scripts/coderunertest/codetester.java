package data.scripts.coderunertest;

import data.scripts.crewReplacer_Job;
import data.scripts.crewReplacer_Main;

//import static data.scripts.crew_replacer.crews;

public class codetester {
    public static void main(String[] args) {
        //startup();
        startup2();
        /*System.out.println("hello workd i guess");
        //fleet.getCargo().addCommodity("crew",500);
        //fleet.getCargo().addCrew(400);
        ArrayList<Float> temp;
        temp = crew_replacer.GetCrewUsed("salvage", (float)125, fleet);
        System.out.println("output: " + temp.size());
        for(int a = 0; a < temp.size(); a++){
            System.out.println("       " + temp.get(a));
        }*/
    }
    private static void startup(){
        /*crew_replacer.addCrewType("crew");//all the ID's are proboly not the currect ID's
        crew_replacer.addCrewType("marines");
        crew_replacer.addCrewType("metals");

        crew_replacer.addJob("fleet",1);
        crew_replacer.addJob("salvage",1);
        crew_replacer.addJob("survey",1);
        crew_replacer.addJob("invade",1);
        crew_replacer.addJob("raid",1);

        crew_replacer.setCrewJob("crew","fleet",true);
        crew_replacer.setCrewJob("crew","salvage",true);
        crew_replacer.setCrewJob("crew","survey",true);
        crew_replacer.setCrewJob("metals","salvage",true);

        crew_replacer.setCrewJob("marines","invade",true);
        crew_replacer.setCrewJob("marines","raid",true);/**/

        //crew_replacer.setCrewPriority("metals",100);
    }
    private static void startup2(){
        crewReplacer_Job tempJob = new crewReplacer_Job();
        tempJob.name = "salvage_main";
        tempJob.addNewCrew("crew",1,10,0,0,true);
        crewReplacer_Main.addOrMergeJob(tempJob);

        tempJob = new crewReplacer_Job();
        tempJob.name = "fleet";
        tempJob.addNewCrew("crew",1,10,0,0,true);
        crewReplacer_Main.addOrMergeJob(tempJob);

        tempJob = new crewReplacer_Job();
        tempJob.name = "survey_main";
        tempJob.addNewCrew("crew",1,10,0,0,true);
        crewReplacer_Main.addOrMergeJob(tempJob);

        /*tempJob = new crewReplacer_Job();
        tempJob.name = "survey_supply";
        tempJob.addNewCrew("crew",1,10,0,0,true);
        crewReplacer_Main.addNewJob(tempJob);

        tempJob = new crewReplacer_Job();
        tempJob.name = "survey_heavyMachinery";
        tempJob.addNewCrew("crew",1,10,0,0,true);
        crewReplacer_Main.addNewJob(tempJob);*/

        tempJob = new crewReplacer_Job();
        tempJob.name = "combat";
        tempJob.addNewCrew("marines",1,10,0,0,true);
        crewReplacer_Main.addOrMergeJob(tempJob);
    }
}
