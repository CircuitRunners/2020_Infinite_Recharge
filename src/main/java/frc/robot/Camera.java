package frc.robot;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Camera {
	private static GripPipeline gripProcessor;
	private static UsbCamera trackingCam;
	// private static UsbCamera elevatorCam;
	private static CvSink camSink;
	private static CvSource liveFeed;
	private static Mat mat;
	private static int frameNumber = 0;
	private static int frameWidth = 80;
	private static int frameHeight = 60;
	// private static double frameDelay = 0.05;

	public static final double verticalFOV = 33.583;// half of the FOV (center to edge)
	public static final double horizontalFOV = 59.703;// half of the FOV (center to edge)
	public static final double diagonalFOV = 68.5;// used math to find the first two from this, this was on the LIFECAM
													// HD-3000 product page

	private static ArrayList<MatOfPoint> contours;
	private static boolean imageTracking = false;
	public static boolean debug = true;

	public static double xAngle = 0;
	public static double yAngle = 0;

	public static void init() {
		Thread cameraOpThread = new Thread(new Runnable() {
			public void run() {
				cameraOperation();
			}
		});
		cameraOpThread.setName("CamThread");
		cameraOpThread.setDaemon(true);
		cameraOpThread.start();
	}

	static boolean targetFound = false;
	static int numContours = 0;
	static int xCenter = 0;
	static int yTop = 0;
	static MatOfPoint contour;
	static Rect box;
	static boolean isElevator = false;

	protected static void cameraOperation() {
		frameNumber = 0;
		trackingCam = CameraServer.getInstance().startAutomaticCapture("cam", 0);
		trackingCam.setResolution(frameWidth, frameHeight);
		trackingCam.setFPS(60);
		gripProcessor = new GripPipeline();
		camSink = CameraServer.getInstance().getVideo(trackingCam);
		liveFeed = CameraServer.getInstance().putVideo("Live", frameWidth, frameHeight);
		trackingCam.setBrightness(50);
		trackingCam.setExposureAuto();
		mat = new Mat();

		while (!Thread.interrupted()) {
			camSink.grabFrame(mat);
			if (!imageTracking) {

			} else {
				targetFound = false;
				gripProcessor.process(mat);
				contours = gripProcessor.convexHullsOutput();
				numContours = contours.size();
				if (numContours > 1) {
					numContours = 1;
				}
				xCenter = yTop = 0;
				if (numContours == 1) {
					if (numContours > 0) {
						for (int i = 0; i < numContours; i++) {
							contour = contours.get(i);
							box = Imgproc.boundingRect(contour);
							xCenter += box.x + (box.width / 2);
							yTop += box.y + (box.height);
						}
						xCenter /= numContours;
						yTop /= numContours;

						xAngle = ((xCenter - (frameWidth / 2)) / (frameWidth / 2)) * horizontalFOV;
						yAngle = ((yTop - (frameHeight / 2)) / (frameHeight / 2)) * verticalFOV;

                        //Imgproc.circle(mat, new Point(xCenter, yTop), 15, new Scalar(235, 55, 15), 2);
                        SmartDashboard.putNumber("xAngle", xAngle);
                        SmartDashboard.putNumber("yAngle",yAngle);
                        targetFound = true;
                        
					}
				} else {
					//stopTracking();
				}

			}
			//liveFeed.putFrame(mat);
			Timer.delay(1 / 30);
		}
		if (Thread.interrupted()) {
			System.out.println("MyCamera: Critical Error, camera thread terminated.");
		}
	}

	public static void startTracking() {
		imageTracking = true;
		setCameras(imageTracking);
		frameNumber = 0;
		numContours = 0;
	}

	public static void stopTracking() {
		xAngle = yAngle = 0;
		imageTracking = false;
		setCameras(imageTracking);
		frameNumber = 0;
		numContours = 0;

	}

	public static boolean isTracking() {
		return imageTracking;
	}

	public static void display() {
		SmartDashboard.putNumber("Frame#:", ++frameNumber);
		SmartDashboard.putNumber("X Angle", xAngle);
		SmartDashboard.putNumber("Y Angle", yAngle);
		SmartDashboard.putNumber("X Center", xCenter);
		SmartDashboard.putNumber("Y Center", yTop);
		SmartDashboard.putNumber("NumContours", numContours);

	}

	public static void setCameras(boolean tracking) {
		if (tracking) {
			trackingCam.setBrightness(10);
			trackingCam.setExposureManual(10);
		} else {
			trackingCam.setBrightness(50);
			trackingCam.setExposureAuto();
		}
	}
}