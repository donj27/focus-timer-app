package com.don.focustimer.data

object ScienceBank {

    data class ScienceQuestion(
        val question: String,
        val options: List<String>,
        val correctIndex: Int
    )

    val questions: List<ScienceQuestion> = listOf(
        // Physics
        ScienceQuestion(
            question = "What is the speed of light in a vacuum?",
            options = listOf("300,000 km/s", "150,000 km/s", "1,000,000 km/s", "30,000 km/s"),
            correctIndex = 0
        ),
        ScienceQuestion(
            question = "What force keeps planets in orbit around the Sun?",
            options = listOf("Electromagnetic force", "Strong nuclear force", "Gravity", "Friction"),
            correctIndex = 2
        ),
        ScienceQuestion(
            question = "What is the SI unit of electrical resistance?",
            options = listOf("Volt", "Ampere", "Watt", "Ohm"),
            correctIndex = 3
        ),
        ScienceQuestion(
            question = "What type of energy does a moving object have?",
            options = listOf("Potential energy", "Kinetic energy", "Thermal energy", "Nuclear energy"),
            correctIndex = 1
        ),
        ScienceQuestion(
            question = "Which color of visible light has the shortest wavelength?",
            options = listOf("Red", "Green", "Blue", "Violet"),
            correctIndex = 3
        ),

        // Chemistry
        ScienceQuestion(
            question = "What is the chemical symbol for gold?",
            options = listOf("Go", "Gd", "Au", "Ag"),
            correctIndex = 2
        ),
        ScienceQuestion(
            question = "How many elements are in the periodic table (as of 2024)?",
            options = listOf("108", "112", "118", "126"),
            correctIndex = 2
        ),
        ScienceQuestion(
            question = "What is the most abundant gas in Earth's atmosphere?",
            options = listOf("Oxygen", "Carbon dioxide", "Nitrogen", "Argon"),
            correctIndex = 2
        ),
        ScienceQuestion(
            question = "What is the pH of pure water?",
            options = listOf("0", "5", "7", "14"),
            correctIndex = 2
        ),
        ScienceQuestion(
            question = "Which element has the atomic number 1?",
            options = listOf("Helium", "Hydrogen", "Lithium", "Carbon"),
            correctIndex = 1
        ),

        // Biology
        ScienceQuestion(
            question = "What organelle is known as the powerhouse of the cell?",
            options = listOf("Nucleus", "Ribosome", "Mitochondria", "Golgi apparatus"),
            correctIndex = 2
        ),
        ScienceQuestion(
            question = "What molecule carries genetic information in most organisms?",
            options = listOf("RNA", "DNA", "ATP", "Protein"),
            correctIndex = 1
        ),
        ScienceQuestion(
            question = "How many chromosomes do humans have?",
            options = listOf("23", "44", "46", "48"),
            correctIndex = 2
        ),
        ScienceQuestion(
            question = "Which organ is responsible for filtering blood in the human body?",
            options = listOf("Liver", "Kidneys", "Heart", "Lungs"),
            correctIndex = 1
        ),
        ScienceQuestion(
            question = "What process do plants use to convert sunlight into energy?",
            options = listOf("Respiration", "Fermentation", "Photosynthesis", "Osmosis"),
            correctIndex = 2
        ),

        // Astronomy
        ScienceQuestion(
            question = "What is the largest planet in our solar system?",
            options = listOf("Saturn", "Neptune", "Jupiter", "Uranus"),
            correctIndex = 2
        ),
        ScienceQuestion(
            question = "What type of star is our Sun?",
            options = listOf("Red dwarf", "White dwarf", "Yellow dwarf", "Blue giant"),
            correctIndex = 2
        ),
        ScienceQuestion(
            question = "How long does it take light from the Sun to reach Earth?",
            options = listOf("About 1 minute", "About 8 minutes", "About 30 minutes", "About 1 hour"),
            correctIndex = 1
        ),
        ScienceQuestion(
            question = "What galaxy do we live in?",
            options = listOf("Andromeda", "Milky Way", "Triangulum", "Sombrero"),
            correctIndex = 1
        ),
        ScienceQuestion(
            question = "Which planet is known as the Red Planet?",
            options = listOf("Venus", "Jupiter", "Mars", "Mercury"),
            correctIndex = 2
        )
    )
}
