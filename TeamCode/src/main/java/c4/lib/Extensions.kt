package c4.lib

/**
 * Increment an Int value.  If the number exceeds our max, go to to min
 *
 * @param max the lower range, inclusive
 * @param min the upper range, inclusive
 */
fun Int.loopingInc(min: Int = 0, max: Int): Int {
    if(this >= max) return min
    return (this + 1)
}

/**
 * Decrement an Int value.  If the number is lower than min, it will go to max.
 *
 * @param max the lower range, inclusive
 * @param min the upper rance, inclusive
 */
fun Int.loopingDec(min: Int = 0, max: Int): Int {
    if(this <= min) return max
    return (this - 1)
}