package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * MainAutonomous.java
 *
 *
 * A Linear OpMode class to be an autonomous method for both Blue & Red alliance sides where we pick
 * which side of the alliance bridge we start off at with gamepad1 as well as selecting alliance
 * color and whether we park under the alliance bridge closer or further from the game field wall.
 * Also will detect the position and deliver the skystone using machine vision and move the
 * foundation.
 *
 * Mec_Odo_AutonomousLevel6_Statey is written to use machine vision and SkyStone delivery to our
 * autonomous route with the help intake jaws that suck in a stone at any orientation using a
 * "touch it-own it" approach.  A servo and two motors make up TC-73/Bucky's arm and stack stones as
 * well as our team marker.

 * This autonomous is used for our State Championship(February 7-8, 2020).
 *
 *
 * @author FTC Team #10273, The Cat in the Hat Comes Back.
 */

@Autonomous(name="MainAutonomous", group="CatAuto")

public class MainAutonomous extends LinearOpMode
{

/* Declare OpMode members. */

    CatHW_Async robot  = new CatHW_Async();    // All the hardware classes init here.
    private ElapsedTime delayTimer = new ElapsedTime();
    private double timeDelay;


    @Override
    public void runOpMode() throws InterruptedException {

        /*
        Initialize the setDrivePowers system variables.  The init() methods of our hardware class
        does all the work:
         */
        FtcDashboard dashboard = FtcDashboard.getInstance();
        Telemetry dashboardTelemetry = dashboard.getTelemetry();

        robot.init(hardwareMap, this, false);



        /*
        Init Delay Option Select:
         */

        // After init is pushed but before Start we can change the delay using dpad up/down //
        delayTimer.reset();
        // Runs a loop to change certain settings while we wait to start
        while (!opModeIsActive() && !isStopRequested()) {
            if (this.isStopRequested()) {
                // Leave the loop if STOP is pressed
                return;
            }
            if (gamepad1.dpad_up && (delayTimer.seconds() > 0.8)) {
                // Increases the amount of time we wait
                timeDelay += 1;
                delayTimer.reset();
            }
            if (gamepad1.dpad_down && (delayTimer.seconds() > 0.8)) {
                // Decreases the amount of time we wait
                if (timeDelay > 0) {
                    // No such thing as negative time
                    timeDelay -= 1;
                }
                delayTimer.reset();
            }
            if (((gamepad1.x) && delayTimer.seconds() > 0.5)) {
                // Changes Alliance Sides
                if (robot.isRedAlliance && !robot.isLeftAlliance) {

                    robot.isRedAlliance = true;
                    robot.isLeftAlliance = true;

                } else if(robot.isRedAlliance && robot.isLeftAlliance) {

                    robot.isLeftAlliance = true;
                    robot.isRedAlliance = false;
                } else if(!robot.isRedAlliance && robot.isLeftAlliance){

                    robot.isLeftAlliance = false;
                    robot.isRedAlliance = false;
                }else if(!robot.isRedAlliance && !robot.isLeftAlliance){

                    robot.isLeftAlliance = false;
                    robot.isRedAlliance = true;
                }
                delayTimer.reset();
            }





            /*
             * LED code:
             */
            if (CatHW_Async.isRedAlliance) {
                robot.lights.setDefaultColor(RevBlinkinLedDriver.BlinkinPattern.RED);
            } else {
                robot.lights.setDefaultColor(RevBlinkinLedDriver.BlinkinPattern.BLUE);
            }



            /*
             * Telemetry while waiting for PLAY:
             */


            telemetry.addData("Delay Timer: ", timeDelay);

            if (robot.isRedAlliance && !robot.isLeftAlliance) {
                telemetry.addData("Alliance: ", "Red Hub");
            } else if(robot.isRedAlliance && robot.isLeftAlliance) {
                telemetry.addData("Alliance: ", "Red Carousal");
            } else if(!robot.isRedAlliance && !robot.isLeftAlliance){
                telemetry.addData("Alliance: ", "Blue Carousal");
            } else if(!robot.isRedAlliance && robot.isLeftAlliance){
                telemetry.addData("Alliance: ", "Blue Hub");
            }

            telemetry.addData("Duck Pos: ", "%s", robot.eyes.getDuckPos().toString());


            dashboardTelemetry.addData("Duck Pos:", "%s", robot.eyes.getDuckPos().toString());

            telemetry.addData("Analysis Right", robot.eyes.pipeline.avg1GetAnalysis());
            telemetry.addData("Analysis Middle", robot.eyes.pipeline.avg2GetAnalysis());
            telemetry.addData("Analysis Left", robot.eyes.pipeline.avg3GetAnalysis());
            dashboardTelemetry.update();

            telemetry.update();


            /*
             * We don't need a "waitForStart()" since we've been running our own
             * loop all this time so that we can make some changes.
             */

        }



        /*
         * Runs after hit start:
         * DO STUFF FOR the OPMODE!!!
         */

        robot.robotWait(timeDelay);
        /*if(!robot.isRedAlliance && robot.isLeftAlliance){
            blueLeft();
        }else if(!robot.isRedAlliance && !robot.isLeftAlliance){
            blueRight();
        }else if(robot.isRedAlliance && robot.isLeftAlliance){
            redRight();


        }else if(robot.isRedAlliance && !robot.isLeftAlliance){
            redLeft();

        }*/
        redRight();






}

    public void blueLeft(){
        CatHW_Vision.UltimateGoalPipeline.duckPosistion duckPos = robot.eyes.getDuckPos();



        switch(duckPos){
            case NONE:
                break;
            case RIGHT:
                robot.jaws.setLiftThird(.5);

                break;
            case MIDDLE:
                robot.jaws.setLiftSecond(.5);
                break;
            case LEFT:
                robot.jaws.setLiftFirst(.5);
                break;
        }
        robot.robotWait(1);
        robot.jaws.dumpPos();
        robot.robotWait(.5);
        robot.jaws.unDump();
        robot.robotWait(.5);
        robot.jaws.setLiftFirst(.5);

        robot.jaws.setIntakeLiftDown();
        robot.robotWait(1);


    }
    public void blueRight(){
        CatHW_Vision.UltimateGoalPipeline.duckPosistion duckPos = robot.eyes.getDuckPos();


        switch(duckPos){
            case NONE:
                break;
            case RIGHT:
                robot.jaws.setLiftThird(.5);

                break;
            case MIDDLE:
                robot.jaws.setLiftSecond(.5);
                break;
            case LEFT:
                robot.jaws.setLiftFirst(.5);
                break;
        }
        robot.robotWait(1);
        robot.jaws.dumpPos();
        robot.robotWait(1);
        robot.jaws.unDump();

        robot.jaws.setLiftFirst(.5);



        robot.carousel.rotateCarousel();
        while(!robot.carousel.isDone()){
            robot.robotWait(0.1);
        }
        robot.robotWait(.5);





    }
    public void redLeft(){

        CatHW_Vision.UltimateGoalPipeline.duckPosistion duckPos = robot.eyes.getDuckPos();
        robot.drive.quickDrive(0,33,0,.9,5);
        robot.drive.quickDrive(11,38,-90,.9,5);

        switch(duckPos){
            case NONE:
                break;
            case RIGHT:
                robot.jaws.setLiftThird(.5);

                break;
            case MIDDLE:
                robot.jaws.setLiftSecond(.5);
                break;
            case LEFT:
                robot.jaws.setLiftFirst(.5);
                break;
        }
        robot.robotWait(1);
        robot.jaws.dumpPos();
        robot.robotWait(1);
        robot.jaws.unDump();
        robot.robotWait(1);
        robot.jaws.setLiftBottom(.5);
        robot.drive.quickDrive(-26,12,90,.9,5);
        robot.robotWait(1);
        robot.drive.quickDrive(-26,32,90,.9,5);


        /*robot.carousel.rotateCarousel();
        while(!robot.carousel.isDone()){
            robot.robotWait(0.1);
        }
        robot.robotWait(.5);*/



    }
    public void redRight(){


        CatHW_Vision.UltimateGoalPipeline.duckPosistion duckPos = robot.eyes.getDuckPos();

        robot.drive.quickDrive(0,33,0,.9,5);
        robot.drive.quickDrive(-5,44,90,.9,5);
        switch(duckPos){
            case NONE:
                break;
            case RIGHT:
                robot.jaws.setLiftThird(.5);

                break;
            case MIDDLE:
                robot.jaws.setLiftSecond(.5);
                break;
            case LEFT:
                robot.jaws.setLiftFirst(.5);
                break;
        }
        robot.robotWait(1);
        robot.jaws.dumpPos();
        robot.robotWait(1);
        robot.jaws.unDump();
        robot.robotWait(1);
        robot.jaws.setLiftFirst(.5);
        robot.drive.quickDrive(-5,2,90,.9,5);
        robot.drive.quickDrive(35,2,90,.5,5);


    }
}