package ftclib;

import com.qualcomm.robotcore.hardware.AccelerationSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import trclib.TrcAccelerometer;
import trclib.TrcDbgTrace;

public class FtcAccelerometer extends TrcAccelerometer
{
    private static final String moduleName = "FtcAccelerometer";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private HardwareMap hardwareMap;
    private AccelerationSensor accelSensor;

    public FtcAccelerometer(HardwareMap hardwareMap, String instanceName, boolean useFilter)
    {
        super(instanceName,
              ACCELOPTION_INTEGRATE_X |
              ACCELOPTION_INTEGRATE_Y |
              ACCELOPTION_INTEGRATE_Z |
              (useFilter? ACCELOPTION_FILTER: 0));

        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        this.hardwareMap = hardwareMap;
        accelSensor = hardwareMap.accelerationSensor.get(instanceName);
        setEnabled(true);
    }   //FtcAccelerometer

    public FtcAccelerometer(String instanceName, boolean useFilter)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName, useFilter);
    }   //FtcAccelerometer

    public FtcAccelerometer(String instanceName)
    {
        this(instanceName, false);
    }   //FtcAccelerometer

    //
    // Implements TrcAccelerometer abstract methods.
    //

    @Override
    public Acceleration getRawAcceleration()
    {
        final String funcName = "getRawAcceleration";
        AccelerationSensor.Acceleration sensorData = accelSensor.getAcceleration();
        Acceleration accelData = new Acceleration(sensorData.x, sensorData.y, sensorData.z);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "! (x=%f,y=%f,z=%f)", accelData.x, accelData.y, accelData.z);
        }

        return accelData;
    }   //getRawAcceleration

}   //class FtcAccelerometer