package c4.subsystems;


import com.qualcomm.robotcore.hardware.Servo;

import org.jetbrains.annotations.Contract;

import java.util.Objects;

public class UniversalFlicker extends Thread {

    public double getPosition() {
        return servo.getPosition();
    }

    private double position;
    private boolean running;
    private boolean isMoving;
    private Servo servo;
    private Double open, close; //'close' can also mean 'down'
    private double speed = 0.03;

    public synchronized double getSpeed() {
        return speed;
    }
    public synchronized void setSpeed(double speed) {
        this.speed = speed;
    }

    public UniversalFlicker(Servo servo, Double open, Double close) {
        this.open = open;
        this.close = close;
        this.servo = servo;

        position = close;
        slowClose();
        running = true;
    }

    /**
     * Open quickly.  This directly sets the servo position.
     */
    public synchronized void fastClose() {
        servo.setPosition(close);
        position = close;
    }
    /**
     * Quickly open the servo.  This directly sets the servo position
     */
    public synchronized void fastOpen() {
        servo.setPosition(open);
        position = open;
    }

    /**
     * Slowly open the servo.  This depends upon {@link #speed} to go quickly or slowly
     */
    public synchronized void slowOpen() {
        position = open;
    }
    /**
     * Slowly close the servo.  This depends on {@link #speed} to determine the speed at which this
     * is completed
     */
    public synchronized void slowClose() {
        position = close;
    }
    /**
     * This slowly goes to a value.  This depends upon {@link #speed} to move quickly or slowly
     * @param val The value at which you will go to.  This must be in [0,1], or the range of the servo
     */
    public synchronized void slowGoToValue(double val) {
        position = val;
    }

    /**
     * Wait for a number of seconds then slowly open.  This is solely used for our arms, but I'm too
     * lazy to make a whole other class, so we're doing this
     * @param secs The seconds this should wait before finally slow moving
     */
    public synchronized void waitThenSlowOpen(double secs) {

        try {
            Thread.sleep((long) (secs * 1000));
        } catch (InterruptedException e) {
            kill();
        }

        slowOpen();
    }
    public synchronized void waitThenFastOpen(double secs) {
        try {
            Thread.sleep((long) (secs * 1000));
        } catch (InterruptedException e) {
            kill();
        }

        fastOpen();
    }

    /**
     * Stop the thread
     */
    public synchronized void kill() {
        running = false;
    }
    /**
     * Is the servo currently moving?
     * @return is the servo currently moving?
     */
    public synchronized boolean isMoving() {
        return isMoving;
    }

    @Override public void run() {
        servo.getPosition();

        servo.setPosition(close);

        while (running) {

            double coef = (position > servo.getPosition())? 1 : -1;

            if(Math.abs(position - servo.getPosition()) < speed) {
                servo.setPosition(position);
                isMoving = true;
            } else {
                servo.setPosition(
                        servo.getPosition() + coef * speed
                );
                isMoving = false;
            }

            try {
                sleep(10);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
