import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

// 식사를 나타내는 클래스를 정의합니다. 이 클래스는 식사의 종류, 이름, 그리고 재료 리스트를 속성으로 갖습니다.
data class Meal(val category: String, val name: String, val ingredients: List<String>)

fun main() {
    val connection = DriverManager.getConnection("jdbc:sqlite:meals.db")
    val statement = connection.createStatement()

    // Create the meals table if it doesn't exist
    statement.execute(
        """
        CREATE TABLE IF NOT EXISTS meals (
            meal_id INTEGER PRIMARY KEY,
            category TEXT,
            meal TEXT
        )
        """
    )

    // Create the ingredients table if it doesn't exist
    statement.execute(
        """
        CREATE TABLE IF NOT EXISTS ingredients (
            ingredient_id INTEGER PRIMARY KEY,
            ingredient TEXT,
            meal_id INTEGER,
            FOREIGN KEY (meal_id) REFERENCES meals (meal_id)
        )
        """
    )

    // 무한 루프를 시작합니다. 사용자가 'exit'를 입력하면 루프를 종료합니다.
    while (true) {
        println("What would you like to do (add, show, exit)?")
        when (readLine()) {
            "add" -> addMeal(statement)
            "show" -> showMeals(statement)
            "exit" -> {
                println("Bye!")
                break
            }
            else -> println("Invalid option. Please try again.")
        }
    }

    connection.close()
}

fun addMeal(statement: Statement) {
    val category = getCategory()
    val name = getName()
    val ingredients = getIngredients()

    statement.execute("INSERT INTO meals (category, meal) VALUES ('$category', '$name')")
    val mealId = statement.generatedKeys.getInt(1)

    for (ingredient in ingredients) {
        statement.execute("INSERT INTO ingredients (ingredient, meal_id) VALUES ('$ingredient', $mealId)")
    }

    println("The meal has been added!")
}

fun showMeals(statement: Statement) {
    val mealsResult = statement.executeQuery(
        """
        SELECT meals.category, meals.meal, ingredients.ingredient
        FROM meals
        JOIN ingredients ON meals.meal_id = ingredients.meal_id
        """
    )

    var currentCategory = ""
    var currentMeal = ""
    var ingredients = mutableListOf<String>()

    while (mealsResult.next()) {
        val category = mealsResult.getString("category")
        val meal = mealsResult.getString("meal")
        val ingredient = mealsResult.getString("ingredient")

        if (category != currentCategory || meal != currentMeal) {
            if (currentCategory.isNotEmpty() && currentMeal.isNotEmpty()) {
                printMeal(currentCategory, currentMeal, ingredients)
            }
            currentCategory = category
            currentMeal = meal
            ingredients = mutableListOf()
        }

        ingredients.add(ingredient)
    }

    if (currentCategory.isNotEmpty() && currentMeal.isNotEmpty()) {
        printMeal(currentCategory, currentMeal, ingredients)
    }

    if (currentCategory.isEmpty() && currentMeal.isEmpty()) {
        println("No meals saved. Add a meal first.")
    }
}

fun printMeal(category: String, meal: String, ingredients: List<String>) {
    println("\nCategory: $category")
    println("Name: $meal")
    println("Ingredients:")
    ingredients.forEach { println(it) }
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
