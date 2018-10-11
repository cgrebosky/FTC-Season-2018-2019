package c4.lib

import android.text.BoringLayout
import android.widget.Button
import com.qualcomm.robotcore.hardware.Gamepad

/**
 * A helper class to make use of the gamepad easier
 */
class ControllerHelper {
    var prevButton = false
    var state = false
    var rawValue = false

    /**
     * Sets up a toggle for a button.
     */
    @SuppressWarnings("unused")
    fun toggle(button: Boolean): Boolean {
        if (button && !prevButton)
            state = !state

        update(button)
        return state
    }

    /**
     * Sets up a "press" system for a button - This will return true only on the initial frame pressed
     */
    @SuppressWarnings("unused")
    fun press(button: Boolean): Boolean {
        state = button && !prevButton

        update(button)
        return state
    }

    private fun update(button: Boolean) {
        rawValue = button
        prevButton = button
    }
}