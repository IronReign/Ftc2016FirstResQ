package ftclib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

import hallib.HalUtil;
import trclib.TrcAnalogInput;
import trclib.TrcDbgTrace;
import trclib.TrcFilter;
import trclib.TrcSensorData;

/**
 * This class implements a platform dependent ultrasonic sensor
 * extending TrcAnalogInput. It provides implementation of the
 * abstract methods in TrcAnalogInput.
 */
public class FtcUltrasonicSensor extends TrcAnalogInput
{
    private static final String moduleName = "FtcUltrasonicSensor";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private UltrasonicSensor sensor;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param hardwareMap specifies the global hardware map.
     * @param instanceName specifies the instance name.
     * @param filter specifies a filter object used for filtering sensor noise.
     *               If none needed, it can be set to null.
     */
    public FtcUltrasonicSensor(HardwareMap hardwareMap, String instanceName, TrcFilter filter)
    {
        super(instanceName, 0, filter);

        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        sensor = hardwareMap.ultrasonicSensor.get(instanceName);
        setEnabled(true);
    }   //FtcUltrasonicSensor

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param filter specifies a filter object used for filtering sensor noise.
     *               If none needed, it can be set to null.
     */
    public FtcUltrasonicSensor(String instanceName, TrcFilter filter)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName, filter);
    }   //FtcUltrasonicSensor

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     */
    public FtcUltrasonicSensor(String instanceName)
    {
        this(instanceName, null);
    }   //FtcUltrasonicSensor

    //
    // Implements TrcAnalogInput abstract methods.
    //

    @Override
    public TrcSensorData getRawData()
    {
        final String funcName = "getRawData";
        TrcSensorData data = new TrcSensorData(
                HalUtil.getCurrentTime(), sensor.getUltrasonicLevel());

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp:%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getRawData

    /**
     * This method returns the raw integrated data which is not supported.
     *
     * @throws UnsupportedOperationException exception.
     */
    @Override
    public TrcSensorData getRawIntegratedData()
    {
        throw new UnsupportedOperationException("This sensor does not support integrated data.");
    }   //getRawIntegratedData

    /**
     * This method returns the raw integrated data which is not supported.
     *
     * @throws UnsupportedOperationException exception.
     */
    @Override
    public TrcSensorData getRawDoubleIntegratedData()
    {
        throw new UnsupportedOperationException(
                "This sensor does not support double integrated data.");
    }   //getRawDoubleIntegratedData

}   //class FtcUltrasonicSensor
