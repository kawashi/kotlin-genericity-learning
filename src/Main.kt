class Test(val value: Any)

fun main(args:Array<String>) {
    val test = Test("test")
    println(test.value.toString())
}