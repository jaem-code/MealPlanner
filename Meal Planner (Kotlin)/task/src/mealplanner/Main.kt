data class Meal(val category: String, val name: String, val ingredients: List<String>)

val meals = mutableListOf<Meal>()

fun main() {
    while (true) {
        println("What would you like to do (add, show, exit)?")
        when (readLine()) {
            "add" -> add()
            "show" -> show()
            "exit" -> {
                println("Bye!")
                return
            }
//            else -> println("What would you like to do (add, show, exit)?")
        }
    }
}

fun add() {
    val category = getCategory()
    val name = getName()
    val ingredients = getIngredients()
    meals.add(Meal(category, name, ingredients))
    println("The meal has been added!")
}

fun show() {
    if (meals.isEmpty()) {
        println("No meals saved. Add a meal first.")
    } else {
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
        if (category in listOf("breakfast", "lunch", "dinner")) {
            return category
        }
        println("Wrong meal category! Choose from: breakfast, lunch, dinner.")
    }
}

fun getName(): String {
    println("Input the meal's name:")
    while (true) {
        val name = readLine()!!
        if (name.isNotBlank() && name.all { it.isLetter() || it.isWhitespace() }) {
            return name
        }
        println("Wrong format. Use letters only!")
    }
}

fun getIngredients(): List<String> {
    println("Input the ingredients:")
    while (true) {
        val ingredients = readLine()!!.split(",").map { it.trim() }
        if (ingredients.all { it.isNotBlank() && it.all { ch -> ch.isLetter() || ch.isWhitespace() } }) {
            return ingredients
        }
        println("Wrong format. Use letters only!")
    }
}
