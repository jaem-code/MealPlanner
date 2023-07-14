import java.sql.DriverManager
import java.sql.Statement

// 식사의 카테고리, 이름, 재료를 담는 데이터 클래스
data class Meal(val category: String, val name: String, val ingredients: List<String>)

fun main() {
    // SQLite 데이터베이스 연결
    val connection = DriverManager.getConnection("jdbc:sqlite:meals.db")
    // SQL 구문을 실행하는 객체 생성
    val statement = connection.createStatement()
    // meals 테이블 생성 (이미 존재하는 경우 무시)
    statement.execute("CREATE TABLE IF NOT EXISTS meals(meal_id INTEGER PRIMARY KEY, category TEXT, meal TEXT)")
    // ingredients 테이블 생성 (이미 존재하는 경우 무시)
    statement.execute("CREATE TABLE IF NOT EXISTS ingredients(ingredient_id INTEGER PRIMARY KEY, meal_id INTEGER, ingredient TEXT)")

    // 무한 루프로 사용자 입력 처리
    while (true) {
        println("What would you like to do (add, show, exit)?")
        val action = readLine()

        when (action) {
            "add" -> addMeal(statement)  // 식사 추가
            "show" -> showMeals(statement)  // 식사 조회
            "exit" -> {
                println("Bye!")
                break  // 프로그램 종료
            }
            else -> println("No meals found.")
        }
    }

    // 데이터베이스 연결 종료
    connection.close()
}

fun addMeal(statement: Statement) {
    println("Which meal do you want to add (breakfast, lunch, dinner)?")
    val category = readLine()

    if (category !in listOf("breakfast", "lunch", "dinner")) {
        println("Wrong meal category! Choose from: breakfast, lunch, dinner.")
        return
    }

    println("Input the meal's name:")
    val meal = readLine()

    println("Input the ingredients:")
    val ingredients = readLine()

    // meals 테이블에서 가장 큰 meal_id를 찾고 1을 더해 새로운 meal_id 생성
    val resultSet = statement.executeQuery("SELECT MAX(meal_id) AS max_id FROM meals")
    val mealId = resultSet.getInt("max_id") + 1

    // 식사를 meals 테이블에 추가
    statement.execute("INSERT INTO meals VALUES($mealId, '$category', '$meal')")

    if (ingredients != null) {
        val ingredientList = ingredients.split(",")
        // 각 재료를 ingredients 테이블에 추가
        for (ingredient in ingredientList) {
            statement.execute("INSERT INTO ingredients(meal_id, ingredient) VALUES($mealId, '${ingredient.trim()}')")
        }
    }

    println("The meal has been added!")
}

fun showMeals(statement: Statement) {
    println("Which category do you want to print (breakfast, lunch, dinner)?")
    val category = readLine()

    if (category !in listOf("breakfast", "lunch", "dinner")) {
        println("Wrong meal category! Choose from: breakfast, lunch, dinner.")
        return
    }

    // 입력받은 카테고리에 해당하는 식사와 그 재료를 조회
    val mealsResult = statement.executeQuery(
        """
        SELECT meals.meal, ingredients.ingredient
        FROM meals
        JOIN ingredients ON meals.meal_id = ingredients.meal_id
        WHERE meals.category = '$category'
        ORDER BY meals.meal_id
        """
    )

    var previousMeal = ""
    var mealFound = false

    while (mealsResult.next()) {
        val mealName = mealsResult.getString("meal")
        val ingredient = mealsResult.getString("ingredient")

        if (mealName != previousMeal) {
            if (!mealFound) {
                // 카테고리 출력
                println("Category: $category")
                mealFound = true
            }
            if (previousMeal.isNotEmpty()) {
                println()
            }
            // 식사 이름 출력
            println("Name: $mealName")
            // 재료 리스트 헤더 출력
            println("Ingredients:")
            previousMeal = mealName
        }

        // 재료 출력
        println(ingredient)
    }

    if (!mealFound) {
        println("No meals found.")
    }
}

// 식사 정보 출력 함수
fun printMeal(category: String, meal: String, ingredients: List<String>) {
    if (category != "") {
        println("\nCategory: $category")
    }
    println("Name: $meal")
    println("Ingredients:")
    ingredients.forEach { println(it) }
}

// 사용자로부터 식사 카테고리를 입력받는 함수
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

// 사용자로부터 식사 이름을 입력받는 함수
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

// 사용자로부터 식사 재료를 입력받는 함수
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
