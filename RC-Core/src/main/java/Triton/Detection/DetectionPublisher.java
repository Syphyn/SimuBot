package Triton.Detection;

import Triton.Vision.VisionData;

import java.util.concurrent.locks.Lock;

import Proto.MessagesRobocupSslDetection.SSL_DetectionFrame;
import Proto.MessagesRobocupSslDetection.SSL_DetectionRobot;

public class DetectionPublisher implements Runnable {

    DetectionData detect = new DetectionData();
    Lock detectionLock;
    public static boolean toggle;

    public DetectionPublisher(Lock detectionLock) {
        this.detectionLock = detectionLock;
    }

    public void run() {
        detectionLock.lock();
        while (true) {
            try {
                toggle();
                update(VisionData.get().getDetection());
                detect.publish();
            } catch (Exception e) {
                //Do nothing
            }
        }
    }

    public void update(SSL_DetectionFrame df) {
        double time = df.getTCapture();
        detect.setBallCount(df.getBallsCount());
        if (detect.getBallCount() > 0) {
            detect.updateBall(df.getBalls(0), time);
        }
        detect.updateTime(time);
        for (SSL_DetectionRobot r : df.getRobotsYellowList()) {
            detect.updateRobot(Team.YELLOW, r.getRobotId(), r, time);
        }
        for (SSL_DetectionRobot r : df.getRobotsBlueList()) {
            detect.updateRobot(Team.BLUE, r.getRobotId(), r, time);
        }
    }

    public void toggle() {
        if (toggle) {
            detectionLock.unlock();
            detectionLock.lock();
        }
    }
}