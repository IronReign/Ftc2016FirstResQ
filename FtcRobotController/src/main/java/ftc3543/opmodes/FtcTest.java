package ftc3543.opmodes;

import ftclib.FtcGamepad;
import ftclib.FtcMenu;
import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcEvent;
import trclib.TrcRobot;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class FtcTest extends FtcOpMode implements FtcMenu.MenuButtons
{
    private HalDashboard dashboard;
    private FtcRobot robot;
    private FtcGamepad driverGamepad;
    //
    // Miscellaneous.
    //
    private TrcStateMachine sm;
    private TrcTimer timer;
    private TrcEvent event;
    //
    // Test menu.
    //
    private static final int TEST_SENSORS           = 0;
    private static final int TEST_DRIVE_TIME        = 1;
    private static final int TEST_DRIVE_DISTANCE    = 2;
    private static final int TEST_TURN_DEGREES      = 3;
    private static final int TEST_LINE_FOLLOWING    = 4;

    private int testChoice = TEST_SENSORS;
    private double driveTime = 0.0;
    private double driveDistance = 0.0;
    private double turnDegrees = 0.0;

    //
    // Implements FtcOpMode abstract methods.
    //

    @Override
    public void robotInit()
    {
        //
        // Initializing global objects.
        //
        dashboard = HalDashboard.getInstance();
        robot = new FtcRobot(TrcRobot.RunMode.TEST_MODE);
        //
        // Initialize input subsystems.
        //
        driverGamepad = new FtcGamepad("DriverGamepad", gamepad1, null);
        driverGamepad.setYInverted(true);
        //
        // Miscellaneous.
        //
        sm = new TrcStateMachine("TestSM");
        timer = new TrcTimer("TestTimer");
        event = new TrcEvent("TestEvent");
        //
        // Choice menus.
        //
        doMenus();
    }   //robotInit

    @Override
    public void startMode()
    {
    }   //startMode

    @Override
    public void stopMode()
    {
    }   //stopMode

    @Override
    public void runPeriodic()
    {
        switch (testChoice)
        {
            case TEST_SENSORS:
                doTestSensors();
                break;

            case TEST_DRIVE_TIME:
                doDriveTime(driveTime);
                break;

            case TEST_DRIVE_DISTANCE:
                doDriveDistance(driveDistance);
                break;

            case TEST_TURN_DEGREES:
                doTurnDegrees(turnDegrees);
                break;

            case TEST_LINE_FOLLOWING:
                doLineFollowing();
                break;
        }
    }   //runPeriodic

    @Override
    public void runContinuous()
    {
    }   //runContinuous

    //
    // Implements MenuButtons
    //

    @Override
    public boolean isMenuUp()
    {
        return gamepad1.dpad_up;
    }   //isMenuUp

    @Override
    public boolean isMenuDown()
    {
        return gamepad1.dpad_down;
    }   //isMenuDown

    @Override
    public boolean isMenuOk()
    {
        return gamepad1.a;
    }   //isMenuOk

    @Override
    public boolean isMenuCancel()
    {
        return gamepad1.b;
    }   //isMenuCancel

    private void doMenus()
    {
        FtcMenu testMenu = new FtcMenu(null, "Tests:", this);
        FtcMenu driveTimeMenu = new FtcMenu(testMenu, "Drive time:", this);
        FtcMenu driveDistanceMenu = new FtcMenu(testMenu, "Drive distance:", this);
        FtcMenu turnDegreesMenu = new FtcMenu(testMenu, "Turn degrees:", this);

        testMenu.addChoice("Test sensors", TEST_SENSORS);
        testMenu.addChoice("Timed Drive", TEST_DRIVE_TIME, driveTimeMenu);
        testMenu.addChoice("Drive x ft", TEST_DRIVE_DISTANCE, driveDistanceMenu);
        testMenu.addChoice("Turn x deg", TEST_TURN_DEGREES, turnDegreesMenu);
        testMenu.addChoice("Line following", TEST_LINE_FOLLOWING);

        driveTimeMenu.addChoice("1 sec", 1.0);
        driveTimeMenu.addChoice("2 sec", 2.0);
        driveTimeMenu.addChoice("4 sec", 4.0);
        driveTimeMenu.addChoice("8 sec", 8.0);

        driveDistanceMenu.addChoice("2 ft", 24.0);
        driveDistanceMenu.addChoice("4 ft", 48.0);
        driveDistanceMenu.addChoice("8 ft", 96.0);
        driveDistanceMenu.addChoice("10 ft", 120.0);

        turnDegreesMenu.addChoice("-90 degrees", -90.0);
        turnDegreesMenu.addChoice("-180 degrees", -180.0);
        turnDegreesMenu.addChoice("-360 degrees", -360.0);
        turnDegreesMenu.addChoice("90 degrees", 90.0);
        turnDegreesMenu.addChoice("180 degrees", 180.0);
        turnDegreesMenu.addChoice("360 degrees", 360.0);

        testMenu.walkMenuTree();

        testChoice = (int)testMenu.getSelectedChoiceValue();
        driveTime = driveTimeMenu.getSelectedChoiceValue();
        driveDistance = driveDistanceMenu.getSelectedChoiceValue();
        turnDegrees = turnDegreesMenu.getSelectedChoiceValue();

        HalDashboard.getInstance().displayPrintf(15, "Test selected = %s",
                                                 testMenu.getSelectedChoiceText());
    }   //doMenus

    private void doTestSensors()
    {
        dashboard.displayPrintf(1, "Testing sensors:");
        double leftPower  = driverGamepad.getLeftStickY(true);
        double rightPower = driverGamepad.getRightStickY(true);
        robot.driveBase.tankDrive(leftPower, rightPower);
        dashboard.displayPrintf(2, "leftPower = %.1f, rightPower = %.1f", leftPower, rightPower);
        dashboard.displayPrintf(3, "Gyro = rate:%f, heading:%f",
                                robot.gyro.getRotation(), robot.gyro.getHeading());
        dashboard.displayPrintf(4, "Color = [R:%d,G:%d,B:%d]",
                                robot.colorSensor.red(),
                                robot.colorSensor.green(),
                                robot.colorSensor.blue());
        dashboard.displayPrintf(5, "RawLightValue = %d",
                                robot.lightSensor.getValue());
        dashboard.displayPrintf(6, "Touch = %s",
                                robot.touchSensor.isActive()? "pressed": "released");
        dashboard.displayPrintf(7, "Sonar = %f", robot.sonarSensor.getUltrasonicLevel());
        dashboard.displayPrintf(8, "lowerLimit=%d, upperLimit=%d",
                                robot.elevator.isLowerLimitSwitchPressed()? 1: 0,
                                robot.elevator.isUpperLimitSwitchPressed()? 1: 0);
    }   //doTestSensors

    private void doDriveTime(double time)
    {
        dashboard.displayPrintf(1, "Drive %.1f sec", time);

        if (sm.isReady())
        {
            int state = sm.getState();

            switch (state)
            {
                case TrcStateMachine.STATE_STARTED:
                    robot.driveBase.tankDrive(0.5, 0.5);
                    timer.set(time, event);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                default:
                    robot.driveBase.stop();
                    sm.stop();
                    break;
            }
        }
    }   //doDriveTime

    private void doDriveDistance(double distance)
    {
        dashboard.displayPrintf(1, "Drive %.1f ft", distance/12.0);

        if (sm.isReady())
        {
            int state = sm.getState();

            switch (state)
            {
                case TrcStateMachine.STATE_STARTED:
                    robot.pidDrive.setTarget(distance, 0.0, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                default:
                    sm.stop();
                    break;
            }
        }
    }   //doDriveDistance

    private void doTurnDegrees(double degrees)
    {
        dashboard.displayPrintf(1, "Turn %.1f degrees", degrees);

        if (sm.isReady())
        {
            int state = sm.getState();

            switch (state)
            {
                case TrcStateMachine.STATE_STARTED:
                    robot.pidDrive.setTarget(0.0, degrees, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                default:
                    sm.stop();
                    break;
            }
        }
    }   //doTurnDegrees

    private void doLineFollowing()
    {
        dashboard.displayPrintf(1, "Line following");

        if (sm.isReady())
        {
            int state = sm.getState();

            switch (state)
            {
                case TrcStateMachine.STATE_STARTED:
                    //
                    // Drive forward until we found the line.
                    //
                    robot.lineTrigger.setEnabled(true);
                    robot.pidDrive.setTarget(120.0, 0.0, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 1:
                    //
                    // Follow the line until the touch switch is activated.
                    //
                    robot.lineTrigger.setEnabled(false);
                    robot.touchTrigger.setEnabled(true);
                    robot.pidCtrlDrive.setOutputRange(-0.3, 0.3);
                    robot.pidCtrlLineFollow.setOutputRange(-0.3, 0.3);
                    robot.pidLineFollow.setTarget(
                            25.0, RobotInfo.LINE_THRESHOLD, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                default:
                    robot.touchTrigger.setEnabled(false);
                    robot.pidCtrlDrive.setOutputRange(-1.0, 1.0);
                    robot.pidCtrlLineFollow.setOutputRange(-1.0, 1.0);
                    sm.stop();
                    break;
            }
        }
    }   //doLineFollowing

}   //class FtcTest