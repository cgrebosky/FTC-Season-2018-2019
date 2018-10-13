package c4.lib

fun Int.loopingInc(min: Int = 0, max: Int): Int {
    if(this >= max) return min
    return (this + 1)
}
fun Int.loopingDec(min: Int = 0, max: Int): Int {
    if(this <= min) return max
    return (this - 1)
}