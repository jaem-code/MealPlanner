// 식사를 나타내는 클래스를 정의합니다. 이 클래스는 식사의 종류, 이름, 그리고 재료 리스트를 속성으로 갖습니다.
data class Meal(val category: String, val name: String, val ingredients: List<String>)

// Meal 객체를 저장할 리스트를 선언합니다. 이 리스트는 mutable로, 요소를 추가하거나 제거할 수 있습니다.
val meals = mutableListOf<Meal>()

fun main() {
    // 무한 루프를 시작합니다. 사용자가 'exit'를 입력하면 루프를 종료합니다.
    while (true) {
        println("What would you like to do (add, show, exit)?")
        when (readLine()) {
            "add" -> add()  // 사용자가 'add'를 입력하면, add() 함수를 호출합니다.
            "show" -> show()  // 사용자가 'show'를 입력하면, show() 함수를 호출합니다.
            "exit" -> {  // 사용자가 'exit'를 입력하면, "Bye!"를 출력하고 프로그램을 종료합니다.
                println("Bye!")
                return
            }
        }
    }
}

fun add() {
    // 각각의 함수를 호출하여 식사의 종류, 이름, 그리고 재료 리스트를 받아옵니다.
    val category = getCategory()
    val name = getName()
    val ingredients = getIngredients()
    // Meal 객체를 생성하여 리스트에 추가합니다.
    meals.add(Meal(category, name, ingredients))
    println("The meal has been added!")
}

fun show() {
    // 리스트가 비어 있으면, "No meals saved. Add a meal first."를 출력합니다.
    if (meals.isEmpty()) {
        println("No meals saved. Add a meal first.")
    } else {
        // 리스트에 있는 각 Meal 객체에 대해, 식사의 종류, 이름, 그리고 재료를 출력합니다.
        meals.forEach { meal ->
            println("\nCategory: ${meal.category}")
            println("Name: ${meal.name}")
            println("Ingredients:")
            meal.ingredients.forEach { println(it) }
        }
    }
}

fun getCategory(): String {
    println("Which meal do you want to add (breakfast, lunch, dinner)?")
    while (true) {
        val category = readLine()!!
        // 입력받은 식사의 종류가 "breakfast", "lunch", "dinner" 중 하나이면, 이를 반환합니다.
        if (category in listOf("breakfast", "lunch", "dinner")) {
            return category
        }
        // 아니라면, "Wrong meal category! Choose from: breakfast, lunch, dinner."를 출력합니다.
        println("Wrong meal category! Choose from: breakfast, lunch, dinner.")
    }
}

fun getName(): String {
    println("Input the meal's name:")
    while (true) {
        val name = readLine()!!
        // 입력받은 이름이 비어 있지 않고, 모든 문자가 알파벳 문자나 공백 문자이면, 이를 반환합니다.
        if (name.isNotBlank() && name.all { it.isLetter() || it.isWhitespace() }) {
            return name
        }
        // 아니라면, "Wrong format. Use letters only!"를 출력합니다.
        println("Wrong format. Use letters only!")
    }
}

fun getIngredients(): List<String> {
    println("Input the ingredients:")
    while (true) {
        // 입력받은 문자열을 쉼표로 분리하고, 각 문자열의 앞뒤 공백을 제거합니다.
        val ingredients = readLine()!!.split(",").map { it.trim() }
        // 각 재료의 이름이 비어 있지 않고, 모든 문자가 알파벳 문자나 공백 문자이면, 이를 반환합니다.
        if (ingredients.all { it.isNotBlank() && it.all { ch -> ch.isLetter() || ch.isWhitespace() } }) {
            return ingredients
        }
        // 아니라면, "Wrong format. Use letters only!"를 출력합니다.
        println("Wrong format. Use letters only!")
    }
}
