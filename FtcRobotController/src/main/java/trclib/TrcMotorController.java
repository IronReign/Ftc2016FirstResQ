package trclib;

/**
 * This class should be extended by a platform dependent motor controller class
 * which will provide all the required abstract methods in this class. The
 * abstract methods allow platform independent access to all the features of
 * the motor controller.
 */
public abstract class TrcMotorController
{
    /**
     * This method sets the motor power. If limit switches are present,
     * it will make sure the motor won't move into the direction where
     * the limit switch is activated.
     *
     * @param power specifies the motor power in the range of -1.0 to 1.0.
     */
    public abstract void setPower(double power);

    /**
     * This method inverts the motor direction.
     *
     * @param inverted specifies true to invert motor direction, false otherwise.
     */
    public abstract void setInverted(boolean inverted);

    /**
     * This method resets the motor position sensor, typically an encoder.
     */
    public abstract void resetPosition();

    /**
     * This method inverts the position sensor direction. This may be rare but
     * there are scenarios where the motor encoder may be mounted somewhere in
     * the power train that it rotates opposite to the motor rotation. This will
     * cause the encoder reading to go down when the motor is receiving positive
     * power. This method can correct this situation.
     *
     * @param inverted specifies true to invert position sensor direction,
     *                 false otherwise.
     */
    public abstract void setPositionSensorInverted(boolean inverted);

    /**
     * This method returns the motor position by reading the position sensor.
     *
     * @return current motor position.
     */
    public abstract double getPosition();

    /**
     * This method returns the speed of the motor rotation.
     *
     * @return motor rotation speed.
     */
    public abstract double getSpeed();

    /**
     * This method returns the state of the reverse limit switch.
     *
     * @return true if reverse limit switch is activated, false otherwise.
     */
    public abstract boolean isReverseLimitSwitchActive();

    /**
     * This method returns the state of the forward limit switch.
     *
     * @return true if forward limit switch is activated, false otherwise.
     */
    public abstract boolean isForwardLimitSwitchActive();

    private static final String moduleName = "TrcMotorController";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private String instanceName;

    /**
     * Constructor: Create an instance of the object.
     *
     * @param instanceName specifies the instance name.
     */
    public TrcMotorController(String instanceName)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        this.instanceName = instanceName;
    }   //TrcMotorController

    /**
     * This method returns the instance name.
     *
     * @return instance name.
     */
    public String toString()
    {
        return instanceName;
    }   //toString

}   //class TrcMotorController
