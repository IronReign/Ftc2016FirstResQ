package ftc3543;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcEvent;
import trclib.TrcRobot;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class AutoParkMountain implements TrcRobot.AutoStrategy
{
    private enum State
    {
        DO_DELAY,
        MOVE_FORWARD,
        TURN_TO_MOUNTAIN,
        GOTO_MOUNTAIN,
        DONE
    }   //enum State

    private FtcRobot robot = ((FtcAuto)FtcOpMode.getInstance()).robot;
    private HalDashboard dashboard = HalDashboard.getInstance();

    private FtcAuto.Alliance alliance;
    private FtcAuto.StartPosition startPos;
    private double delay;
    private TrcEvent event;
    private TrcTimer timer;
    private TrcStateMachine sm;

    public AutoParkMountain(FtcAuto.Alliance alliance, FtcAuto.StartPosition startPos, double delay)
    {
        this.alliance = alliance;
        this.startPos = startPos;
        this.delay = delay;
        event = new TrcEvent("ParkMountainEvent");
        timer = new TrcTimer("ParkMountainTimer");
        sm = new TrcStateMachine("autoParkMountain");
        sm.start(State.DO_DELAY);
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "ParkMountain: %s, %s, delay=%.0f",
                                alliance.toString(), startPos.toString(), delay);

        if (sm.isReady())
        {
            State state = (State)sm.getState();

            switch (state)
            {
                case DO_DELAY:
                    //
                    // If there is a delay, set the timer for it.
                    //
                    if (delay == 0.0)
                    {
                        sm.setState(State.MOVE_FORWARD);
                    }
                    else
                    {
                        timer.set(delay, event);
                        sm.addEvent(event);
                        sm.waitForEvents(State.MOVE_FORWARD);
                    }
                    break;

                case MOVE_FORWARD:
                    //
                    // Move forward towards the mountain.
                    //
                    robot.pidDrive.setTarget(
                            startPos == FtcAuto.StartPosition.NEAR_MOUNTAIN? 50.0: 76.0, 0.0,
                            false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.TURN_TO_MOUNTAIN);
                    break;

                case TURN_TO_MOUNTAIN:
                    //
                    // Turn to face the mountain.
                    //
                    robot.pidDrive.setTarget(
                            0.0, alliance == FtcAuto.Alliance.RED_ALLIANCE? -90.0: 90.0,
                            false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.GOTO_MOUNTAIN);
                    break;

                case GOTO_MOUNTAIN:
                    //
                    // Turn on the tread drive and run as hard as you could
                    // to climb up the mountain
                    //
                    robot.pidDrive.setTarget(
                            startPos == FtcAuto.StartPosition.NEAR_MOUNTAIN? 50.0: 70.0, 0.0,
                            false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.DONE);
                    break;

                case DONE:
                default:
                    //
                    // We are done, stop!
                    //
                    sm.stop();
                    break;
            }
        }
    }

}   //class AutoParkMountain
