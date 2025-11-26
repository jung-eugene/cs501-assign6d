# CS 501 Individual Assignment 6 Question 4 — Gyroscope-Controlled Ball Game

## Explanation

The **Gyroscope-Controlled Ball Game** uses real gyroscope sensor data to move a ball across the screen.
As the device tilts, the ball moves smoothly in that direction. A simple rectangular obstacle acts as a wall that the ball cannot pass through, creating a minimal maze-like environment.
Rotation values from the gyroscope are integrated into tilt values, which then update the ball’s position on a `Canvas`.

## How to Use

1. Launch the app in the **Android Emulator**.
2. Open **Extended Controls → Virtual Sensors**.
3. Use:
   * **X-Rot slider** → tilt forward/back (moves the ball up/down)
   * **Y-Rot slider** → tilt left/right (moves the ball left/right)
4. Watch the ball move around the screen in real time based on tilt direction.
5. Try navigating the ball around the **gray obstacle** in the middle—collision detection prevents the ball from passing through it.

## Implementation

* **Gyroscope Listener**
  `SensorEventListener` receives angular velocity from the gyroscope.
  Roll and pitch values are updated by integrating these velocities over time.

* **Game Logic**
  A `Canvas` draws:
  * a red circle representing the ball
  * a gray rectangle serving as a wall/obstacle
    Position updates are clamped to screen bounds, and a simple bounding-box collision check prevents the ball from entering the obstacle.

* **State Management**
  `remember` state variables store ball position and update smoothly as tilt changes.

* **Testing With Virtual Sensors**
  The emulator’s rotation sliders directly update the gyroscope values, allowing full interaction without needing a physical device.
