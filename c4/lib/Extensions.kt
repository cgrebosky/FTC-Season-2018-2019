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

/**
 * Calculates absolute difference between 2 numbers
 */
fun error(val1: Int, val2: Int): Int = Math.abs(val1 - val2)

/**
 * Calculates percent error between 2 numbers.  Note this is on the scale of [0-1], not [0-100]
 */
fun pctError(benchmark: Int, experimental: Int): Double = error(benchmark, experimental).toDouble() / benchmark.toDouble()