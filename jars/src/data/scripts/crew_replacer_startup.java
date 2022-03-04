package data.scripts;
import com.fs.starfarer.api.BaseModPlugin;

import java.util.ArrayList;
/*
    HERE
    THIS IS INPOSSABLE:
    as far as im able to tell, any data i save in crewReplacer_Main is saved FOREVER. i have no FUCKING CLUE how to reset said data.
    i tried crewReplacer_Main.Jobs = new ArrayList<crewReplacer_Job>(); to empty the data out, but data IM SHOULD NOT BE SAVEING RIGHT NOW is STILL SAVEING
    AAAAAAAAAAAAAAAAAAAAAAAAAAA
    (i was compieling my project wong.)(it now works perfictly.)

 */
public class crew_replacer_startup extends BaseModPlugin {
    @Override
    public void onApplicationLoad() {
        //crewReplacer_Main.Jobs = new ArrayList<crewReplacer_Job>();
        startup2();
        /**/
        //crew_replacer.reset();

        /*crew_replacer.addCrewType("crew");//all the ID's are proboly not the currect ID's
        crew_replacer.addCrewType("marines");
        //crew_replacer.addCrewType("metals");

        crew_replacer.addJob("fleet",1);
        crew_replacer.addJob("salvage",1);
        crew_replacer.addJob("survey",1);
        crew_replacer.addJob("invade",1);
        crew_replacer.addJob("raid",1);/**/

        /*crew_replacer.addCrewType("crew");//all the ID's are proboly not the currect ID's
        crew_replacer.addCrewType("marines");
        crew_replacer.addCrewType("metals");


        crew_replacer.addJob("fleet",1);
        crew_replacer.addJob("salvage",1);
        crew_replacer.addJob("survey",1);
        crew_replacer.addJob("invade",1);
        crew_replacer.addJob("raid",1);


        crew_replacer.setCrewJob("crew","salvage",true);
        crew_replacer.setCrewJob("crew","survey",true);

        crew_replacer.setCrewJob("marines","invade",true);
        crew_replacer.setCrewJob("marines","raid",true);


        crew_replacer.addCrewType("hate");//crew_replacer.addcrewtype AFTER crew_replacer addJob resalts in crash.
        crew_replacer.setCrewJob("hate","salvage",true);/**/

    }

    @Override
    public void onGameLoad(boolean newGame) {
        crewReplacer_Main.organizePriority();

        /*/crew_replacer.setCrewJob("crew","fleet",true);
        crew_replacer.setCrewJob("crew","salvage",true);
        crew_replacer.setCrewJob("crew","survey",true);

        crew_replacer.setCrewJob("marines","invade",true);
        crew_replacer.setCrewJob("marines","raid",true);

        //crew_replacer.setCrewJob("metals","salvage",true);/**/
        super.onGameLoad(newGame);
    }
    private static void startup2(){
        crewReplacer_Job tempJob = crewReplacer_Main.getJob("salvage_main");
        tempJob.addNewCrew("crew",1,10/*,0,0,true*/);

        tempJob = crewReplacer_Main.getJob("salvage_Secondary");
        tempJob.addNewCrew("heavy_machinery",1,10/*,0,0,true*/);

        tempJob = crewReplacer_Main.getJob("fleet");
        tempJob.addNewCrew("crew",1,10/*,0,0,true*/);

        tempJob = crewReplacer_Main.getJob("survey_main");
        tempJob.addNewCrew("crew",1,10/*,0,0,true*/);

        //crewReplacer_Main.addOrMergeJob(tempJob);

        /*tempJob = new crewReplacer_Job();
        tempJob.name = "survey_supply";
        tempJob.addNewCrew("crew",1,10,0,0,true);
        crewReplacer_Main.addNewJob(tempJob);

        tempJob = new crewReplacer_Job();
        tempJob.name = "survey_heavyMachinery";
        tempJob.addNewCrew("crew",1,10,0,0,true);
        crewReplacer_Main.addNewJob(tempJob);*/

        tempJob = crewReplacer_Main.getJob("combat");
        tempJob.addNewCrew("marines",1,10/*,0,0,true*/);
    }
}