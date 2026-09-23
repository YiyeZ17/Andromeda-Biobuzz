package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(group = "movement")
public class MecanumDrive extends OpMode {

    private DcMotorEx frontLeft, frontRight, backLeft, backRight;
    private IMU imu;

    @Override
    public void init() {

        // INITIALIZING MOTORS

        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");

        frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
        backLeft.setDirection(DcMotorEx.Direction.REVERSE);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // INITIALIZING GYRO

        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot orientation = new RevHubOrientationOnRobot(

                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD

        );

        imu.initialize(new IMU.Parameters(orientation));

    }

    @Override
    public void loop() {

        double forward = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double rotation = gamepad1.right_stick_x;

        driveRelative(forward, strafe, rotation);

    }

    public void drive(double forward, double strafe, double rotation) {

        double maxPower = 1.0;

        double frontLeftPower = forward + strafe + rotation;
        double frontRightPower = forward - strafe - rotation;
        double backLeftPower = forward - strafe + rotation;
        double backRightPower = forward + strafe - rotation;

        double[] powers = {frontLeftPower, frontRightPower, backLeftPower, backRightPower};

        for (double power : powers) {

            maxPower = Math.max(maxPower, Math.abs(power));

        }

        frontLeft.setPower(frontLeftPower / maxPower);
        frontRight.setPower(frontRightPower / maxPower);
        backLeft.setPower(backLeftPower / maxPower);
        backRight.setPower(backRightPower / maxPower);

    }

    public void driveRelative(double forward, double strafe, double rotation) {

        double theta = Math.atan2(forward, strafe);
        double radius = Math.hypot(forward, strafe);

        double yaw = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        theta = AngleUnit.normalizeRadians(theta - yaw);

        forward = radius * Math.sin(theta);
        strafe = radius * Math.cos(theta);

        drive(forward, strafe, rotation);

    }

}
