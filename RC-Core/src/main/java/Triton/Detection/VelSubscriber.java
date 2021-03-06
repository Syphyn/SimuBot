package Triton.Detection;

public class VelSubscriber implements Runnable {

    public void run() {
        while(true) {
            try {
                System.out.println("Blue 1 Velocity: " + 
                    DetectionData.get().getRobotVel(Team.BLUE, 1));
            } catch (Exception e) {
                // do nothing
            }
        }
    }
}