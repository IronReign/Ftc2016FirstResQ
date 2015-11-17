package trclib;

import hallib.HalUtil;

public abstract class TrcAccelerometer
{
    public abstract TrcSensorAxisData getRawAccelerations();

    private class RawAccelX implements TrcFilteredSensor
    {
        private TrcKalmanFilter kalman = null;
        private double sign = 1.0;

        public RawAccelX(boolean useFilter)
        {
            if (useFilter)
            {
                kalman = new TrcKalmanFilter();
            }
        }   //RawAccelX

        public void setInverted(boolean inverted)
        {
            sign = inverted? -1.0: 1.0;
        }   //setInverted

        public TrcSensorData getRawValue()
        {
            updateAccelerationData();
            double value = sign*accelData.x;
            return new TrcSensorData(value, accelData.timestamp);
        }   //getRawValue

        public TrcSensorData getFilteredValue()
        {
            updateAccelerationData();
            double value = sign*filterData(kalman, accelData.x, xZeroOffset, xDeadband);
            return new TrcSensorData(value, accelData.timestamp);
        }   //getFilteredValue
    }   //class RawAccelX

    private class RawAccelY implements TrcFilteredSensor
    {
        private TrcKalmanFilter kalman = null;
        private double sign = 1.0;

        public RawAccelY(boolean useFilter)
        {
            if (useFilter)
            {
                kalman = new TrcKalmanFilter();
            }
        }   //RawAccelX

        public void setInverted(boolean inverted)
        {
            sign = inverted? -1.0: 1.0;
        }   //setInverted

        public TrcSensorData getRawValue()
        {
            updateAccelerationData();
            double value = sign*accelData.y;
            return new TrcSensorData(value, accelData.timestamp);
        }   //getRawValue

        public TrcSensorData getFilteredValue()
        {
            updateAccelerationData();
            double value = sign*filterData(kalman, accelData.y, yZeroOffset, yDeadband);
            return new TrcSensorData(value, accelData.timestamp);
        }   //getFilteredValue
    }   //class RawAccelY

    private class RawAccelZ implements TrcFilteredSensor
    {
        private TrcKalmanFilter kalman = null;
        private double sign = 1.0;

        public RawAccelZ(boolean useFilter)
        {
            if (useFilter)
            {
                kalman = new TrcKalmanFilter();
            }
        }   //RawAccelX

        public void setInverted(boolean inverted)
        {
            sign = inverted? -1.0: 1.0;
        }   //setInverted

        public TrcSensorData getRawValue()
        {
            updateAccelerationData();
            double value = sign*accelData.z;
            return new TrcSensorData(value, accelData.timestamp);
        }   //getRawValue

        public TrcSensorData getFilteredValue()
        {
            updateAccelerationData();
            double value = sign*filterData(kalman, accelData.z, zZeroOffset, zDeadband);
            return new TrcSensorData(value, accelData.timestamp);
        }   //getFilteredValue
    }   //class RawAccelZ

    private double filterData(
            TrcKalmanFilter kalman, double value, double zeroOffset, double deadband)
    {
        value = TrcUtil.applyDeadband(value - zeroOffset, deadband);
        if (kalman != null)
        {
            value = kalman.filter(value);
        }
        return value;
    }   //filterData

    public static final int ACCELOPTION_INTEGRATE_X         = (1 << 0);
    public static final int ACCELOPTION_INTEGRATE_Y         = (1 << 1);
    public static final int ACCELOPTION_INTEGRATE_Z         = (1 << 2);
    public static final int ACCELOPTION_FILTER              = (1 << 3);

    private static final String moduleName = "TrcAccelerometer";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private static final int NUM_CAL_SAMPLES = 100;

    private String instanceName;
    private RawAccelX rawAccelX = null;
    private RawAccelY rawAccelY = null;
    private RawAccelZ rawAccelZ = null;
    private TrcIntegrator xIntegrator = null;
    private TrcIntegrator yIntegrator = null;
    private TrcIntegrator zIntegrator = null;
    private double xZeroOffset = 0.0;
    private double xDeadband = 0.0;
    private double yZeroOffset = 0.0;
    private double yDeadband = 0.0;
    private double zZeroOffset = 0.0;
    private double zDeadband = 0.0;
    private boolean calibrating = false;
    private TrcSensorAxisData accelData = null;
    private double dataStaleTime = 0.005;

    public TrcAccelerometer(String instanceName, int options)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        this.instanceName = instanceName;
        boolean useFilter = (options & ACCELOPTION_FILTER) != 0;
        rawAccelX = new RawAccelX(useFilter);
        rawAccelY = new RawAccelY(useFilter);
        rawAccelZ = new RawAccelZ(useFilter);

        if ((options & ACCELOPTION_INTEGRATE_X) != 0)
        {
            xIntegrator = new TrcIntegrator("x" + instanceName, rawAccelX, true);
        }

        if ((options & ACCELOPTION_INTEGRATE_Y) != 0)
        {
            yIntegrator = new TrcIntegrator("y" + instanceName, rawAccelY, true);
        }

        if ((options & ACCELOPTION_INTEGRATE_Z) != 0)
        {
            zIntegrator = new TrcIntegrator("z" + instanceName, rawAccelZ, true);
        }
    }   //TrcAccelerometer

    public TrcAccelerometer(String instanceName)
    {
        this(instanceName, 0);
    }   //TrcAccelerometer

    public String toString()
    {
        return instanceName;
    }   //toString

    public void setDataStaleTime(double dataStaleTime)
    {
        final String funcName = "setDataStaleTime";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "staleTime=%f", dataStaleTime);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.dataStaleTime = dataStaleTime;
    }   //setDataStaleTime

    private void updateAccelerationData()
    {
        final String funcName = "updateAccelerationData";

        if (accelData == null ||
            HalUtil.getCurrentTime() - accelData.timestamp > dataStaleTime)
        {
            accelData = getRawAccelerations();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC);
        }
    }   //updateAccelerationData

    public void setEnabled(boolean enabled)
    {
        final String funcName = "setEnabled";
        boolean needCalibration = false;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(enabled));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (xIntegrator != null)
        {
            xIntegrator.setEnabled(enabled);
            needCalibration = true;
        }

        if (yIntegrator != null)
        {
            yIntegrator.setEnabled(enabled);
            needCalibration = true;
        }

        if (zIntegrator != null)
        {
            zIntegrator.setEnabled(enabled);
            needCalibration = true;
        }
    }   //setEnabled

    public void setXInverted(boolean inverted)
    {
        final String funcName = "setXInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        rawAccelX.setInverted(inverted);
    }   //setXInverted

    public void setYInverted(boolean inverted)
    {
        final String funcName = "setYInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        rawAccelY.setInverted(inverted);
    }   //setYInverted

    public void setZInverted(boolean inverted)
    {
        final String funcName = "setZInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        rawAccelZ.setInverted(inverted);
    }   //setZInverted

    public void calibrate()
    {
        final String funcName = "calibrate";
        TrcSensorAxisData data;

        xZeroOffset = 0.0;
        xDeadband = 0.0;
        yZeroOffset = 0.0;
        yDeadband = 0.0;
        zZeroOffset = 0.0;
        zDeadband = 0.0;
        data = getRawAccelerations();
        double xMinValue = data.x;
        double xMaxValue = xMinValue;
        double xSum = 0.0;
        double yMinValue = data.y;
        double yMaxValue = yMinValue;
        double ySum = 0.0;
        double zMinValue = data.z;
        double zMaxValue = zMinValue;
        double zSum = 0.0;

        calibrating = true;
        for (int i = 0; i < NUM_CAL_SAMPLES; i++)
        {
            data = getRawAccelerations();
            xSum += data.x;
            ySum += data.y;
            zSum += data.z;

            if (data.x < xMinValue)
            {
                xMinValue = data.x;
            }
            else if (data.x > xMaxValue)
            {
                xMaxValue = data.x;
            }

            if (data.y < yMinValue)
            {
                yMinValue = data.y;
            }
            else if (data.y > yMaxValue)
            {
                yMaxValue = data.y;
            }

            if (data.z < zMinValue)
            {
                zMinValue = data.z;
            }
            else if (data.z > zMaxValue)
            {
                zMaxValue = data.z;
            }

            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
            }
        }

        xZeroOffset = xSum/NUM_CAL_SAMPLES;
        xDeadband = (xMaxValue - xMinValue);
        yZeroOffset = ySum/NUM_CAL_SAMPLES;
        yDeadband = (yMaxValue - yMinValue);
        zZeroOffset = zSum/NUM_CAL_SAMPLES;
        zDeadband = (zMaxValue - zMinValue);
        calibrating = false;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "! (x:%.1f,%.1f y:%.1f,%.1f z:%.1f,%.1f)",
                               xZeroOffset, xDeadband,
                               yZeroOffset, yDeadband,
                               zZeroOffset, zDeadband);
        }
    }   //calibrate

    public boolean isCalibrating()
    {
        final String funcName = "isCalibrating";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", Boolean.toString(calibrating));
        }

        return calibrating;
    }   //isCalibrating

    public void resetXIntegrator()
    {
        final String funcName = "resetXIntegrator";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (xIntegrator != null)
        {
            xIntegrator.reset();
        }
    }   //resetXIntegrator

    public void resetYIntegrator()
    {
        final String funcName = "resetYIntegrator";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (yIntegrator != null)
        {
            yIntegrator.reset();
        }
    }   //resetYIntegrator

    public void resetZIntegrator()
    {
        final String funcName = "resetZIntegrator";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (zIntegrator != null)
        {
            zIntegrator.reset();
        }
    }   //resetZIntegrator

    public double getXAcceleration()
    {
        final String funcName = "getXAcceleration";
        double value = rawAccelX.getFilteredValue().data;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getXAcceleration

    public double getYAcceleration()
    {
        final String funcName = "getYAcceleration";
        double value = rawAccelY.getFilteredValue().data;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getYAcceleration

    public double getZAcceleration()
    {
        final String funcName = "getZAcceleration";
        double value = rawAccelZ.getFilteredValue().data;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getZAcceleration

    public double getXVelocity()
    {
        final String funcName = "getXVelocity";
        double value = 0.0;

        if (xIntegrator != null)
        {
            value = xIntegrator.getIntermediateOutput();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getXVelocity

    public double getYVelocity()
    {
        final String funcName = "getYVelocity";
        double value = 0.0;

        if (yIntegrator != null)
        {
            value = yIntegrator.getIntermediateOutput();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getYVelocity

    public double getZVelocity()
    {
        final String funcName = "getZVelocity";
        double value = 0.0;

        if (zIntegrator != null)
        {
            value = zIntegrator.getIntermediateOutput();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getZVelocity

    public double getXDistance()
    {
        final String funcName = "getXDistance";
        double value = 0.0;

        if (xIntegrator != null)
        {
            value = xIntegrator.getOutput();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getXDistance

    public double getYDistance()
    {
        final String funcName = "getYDistance";
        double value = 0.0;

        if (yIntegrator != null)
        {
            value = yIntegrator.getOutput();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getYDistance

    public double getZDistance()
    {
        final String funcName = "getZDistance";
        double value = 0.0;

        if (zIntegrator != null)
        {
            value = zIntegrator.getOutput();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getZDistance

}   //class TrcAccelerometer
