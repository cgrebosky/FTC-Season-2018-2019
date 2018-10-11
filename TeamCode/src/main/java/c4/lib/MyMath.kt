package c4.lib

object MyMath {
    fun limitNumber(num: Int, low: Int = 0, high: Int): Int
    {
        var tmp = num
        if (num < low) tmp = low
        if (num > high) tmp = high
        return tmp
    }

    fun loopNumber(num: Int, low: Int = 0, high: Int): Int {
        if(num < low) return high - (low - num)
        if(num > high) return low + (num - high)
        return num
    }
}