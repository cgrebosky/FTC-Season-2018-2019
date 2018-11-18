package c4.lib

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
    fun toggle(button: Boolean): Boolean {
        if (button && !prevButton)
            state = !state

        update(button)
        return state
    }

    /**
     * Sets up a "press" system for a button - This will return true only on the initial frame pressed
     */
    fun press(button: Boolean): Boolean {
        state = button && !prevButton

        update(button)
        return state
    }

    fun release(button: Boolean): Boolean {
        state = prevButton && !button

        update(button)
        return state
    }

    /**
     * Update the variable values.  Internal method
     */
    private fun update(button: Boolean) {
        rawValue = button
        prevButton = button
    }
}